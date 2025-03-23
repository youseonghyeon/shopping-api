package com.shop.shoppingapi.producer;

import com.shop.shoppingapi.producer.dto.DeliveryMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 추후 서비스의 유연성 및 확장성을 고려하여 Kafka Worker Manager를 구현합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class KafkaWorkerManager {

    private final KafkaOrderMessageQueue kafkaOrderMessageQueue;
    private final KafkaOrderProducer producer;

    private final int WORKER_THREAD_COUNT = 2;
    private final List<Thread> workers = new ArrayList<>();

    private final ScheduledExecutorService retryScheduler = Executors.newScheduledThreadPool(1);

    /// 5초의 딜레이가 필요하므로 @PostConstruct 대신 @EventListener(ApplicationReadyEvent)를 사용합니다.
    @EventListener(ApplicationReadyEvent.class)
    public void kafkaOrderMessageQueueWorkerStart() {
        for (int i = 0; i < WORKER_THREAD_COUNT; i++) {
            createAndStartWorker(i);
        }
        Thread monitorThread = new Thread(() -> {
            while (true) {
                for (int i = 0; i < workers.size(); i++) {
                    Thread t = workers.get(i);
                    if (!t.isAlive()) {
                        log.warn("[KafkaWorkerManager] Worker thread '{}' is not alive. Restarting...", t.getName());
                        createAndStartWorker(i);
                    }
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                    log.error("[KafkaWorkerManager] Kafka Worker Manager Thread Interrupted");
                }
            }
        }, "kafka-worker-monitor");
        monitorThread.setUncaughtExceptionHandler((t, e) -> log.error("[KafkaWorkerManager] Monitor thread crashed unexpectedly", e));
        monitorThread.start();

        sleep(5000);
        log.info("[KafkaWorkerManager] Current Kafka Thread info: monitorThread: {}, workers: {}", monitorThread, workers);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("[KafkaWorkerManager] Sleep interrupted", e);
        }
    }

    private void createAndStartWorker(int index) {
        Thread worker = new Thread(() -> {
            log.info("[KafkaWorkerManager] Worker thread '{}' started", index);
            while (true) {
                DeliveryMessage msg = null;
                try {
                    msg = kafkaOrderMessageQueue.take();
                    producer.sendMessage(msg);
                } catch (Exception e) {
                    log.error("[KafkaWorkerManager] Failed to send Kafka message. Will retry after delay.", e);
                    if (msg != null) {
                        retryWithDelay(msg);
                    } else {
                        log.error("[KafkaWorkerManager] Failed to send Kafka message. Message is null.");
                    }
                }
            }
        }, "kafka-worker-" + index);

        worker.setDaemon(true);
        worker.setUncaughtExceptionHandler((t, e) -> log.error("[KafkaWorkerManager] Uncaught exception in thread '{}'", t.getName(), e));
        worker.start();
        if (index < workers.size()) {
            workers.set(index, worker);
        } else {
            workers.add(worker);
        }
    }

    private void retryWithDelay(DeliveryMessage msg) {
        retryScheduler.schedule(() -> {
            boolean result = kafkaOrderMessageQueue.enqueue(msg);
            if (!result) {
                log.error("[KafkaWorkerManager] Failed to re-enqueue Kafka message after retry delay. Message dropped.");
            } else {
                log.debug("[KafkaWorkerManager] Kafka message re-enqueued after retry delay.");
            }
        }, 5, TimeUnit.SECONDS);
    }
}

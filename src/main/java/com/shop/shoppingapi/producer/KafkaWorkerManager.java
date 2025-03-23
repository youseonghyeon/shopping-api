package com.shop.shoppingapi.producer;

import com.shop.shoppingapi.producer.dto.DeliveryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 추후 서비스의 유연성 및 확장성을 고려하여 Kafka Worker Manager를 구현합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaWorkerManager {

    private final KafkaOrderMessageQueue kafkaOrderMessageQueue;
    private final KafkaOrderProducer producer;

    private final int WORKER_THREAD_COUNT = 2;
    private final List<Thread> workers = new ArrayList<>();

    private final ScheduledExecutorService retryScheduler = Executors.newScheduledThreadPool(1);

    public void start() {
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

        if (workers.stream().allMatch(Thread::isAlive)) {
            log.info("[KafkaWorkerManager] All Kafka worker threads successfully started.");
        } else {
            log.warn("[KafkaWorkerManager] Some Kafka worker threads failed to start properly.");
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

    // Kafka Worker Manager의 상태를 확인하여 모든 Worker Thread가 정상적으로 동작 중인지 확인합니다.
    public boolean isHealthy() {
        return workers.size() == WORKER_THREAD_COUNT &&
                workers.stream().allMatch(t -> t != null && t.isAlive());
    }
}

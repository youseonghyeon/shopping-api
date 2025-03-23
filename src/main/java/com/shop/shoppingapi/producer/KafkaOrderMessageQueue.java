package com.shop.shoppingapi.producer;

import com.shop.shoppingapi.producer.dto.DeliveryMessage;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KafkaOrderMessageQueue {

    private final BlockingQueue<DeliveryMessage> queue = new LinkedBlockingQueue<>(10000);

    public boolean enqueue(DeliveryMessage message) {
        return queue.offer(message);
    }

   public DeliveryMessage take() throws InterruptedException {
        return queue.take();
    }

    @PreDestroy
    public void onShutdown() {
        // TODO 서비스 재기동간 백업 정책 생성 필요 (nosqlDB 또는 file 등) 서비스 마이그레이션도 고려하여 구현
        log.info("[KafkaOrderMessageQueue] Kafka Order Message Queue is shutting down... Remaining messages count: {}", queue.size());
    }
}

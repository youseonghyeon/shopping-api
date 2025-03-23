package com.shop.shoppingapi.producer;

import com.shop.shoppingapi.producer.dto.DeliveryMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMessageEventListener {

    private final KafkaOrderMessageQueue queue;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderSubmittedEvent(DeliveryMessage event) {
        log.info("Order submitted event received: {}", event);
        boolean accepted = queue.enqueue(event);
        if (!accepted) {
            log.error("Kafka message queue is full. Potential message loss detected.");
        }
    }
}

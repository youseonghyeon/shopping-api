package com.shop.shoppingapi.producer;

import com.shop.shoppingapi.producer.dto.DeliveryMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaOrderProducer {

    private final KafkaTemplate<String, DeliveryMessage> kafkaTemplate;
    private final String TOPIC = "delivery-topic";

    public void sendMessage(DeliveryMessage message) {
        log.info("Sending message to topic: {}, message: {}", TOPIC, message);
        kafkaTemplate.send(TOPIC, message);
    }
}

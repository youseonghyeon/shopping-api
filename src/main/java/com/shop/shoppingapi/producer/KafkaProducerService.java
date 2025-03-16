package com.shop.shoppingapi.producer;

import com.shop.shoppingapi.producer.dto.KafkaMessageDtoSample;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, KafkaMessageDtoSample> kafkaTemplate;

    public void sendMessage(String topic, String key, KafkaMessageDtoSample message) {
        log.info("Sending message to topic: {}, key: {}, message: {}", topic, key, message);
        kafkaTemplate.send(topic, key, message);
    }
}

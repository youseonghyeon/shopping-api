package com.shop.shoppingapi.config;


import com.shop.shoppingapi.producer.KafkaOrderMessageQueue;
import com.shop.shoppingapi.producer.KafkaOrderProducer;
import com.shop.shoppingapi.producer.KafkaWorkerManager;
import com.shop.shoppingapi.producer.dto.DeliveryMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks:all}")
    private String acks;

    @Value("${spring.kafka.producer.retries:0}")
    private int retries;

    @Value("${spring.kafka.producer.batch-size:16384}")
    private int batchSize;

    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private int bufferMemory;

    @Bean
    public ProducerFactory<String, DeliveryMessage> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, DeliveryMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /// fail-fast 를 사용하여 Thread 정상 기동이 아니면 서비스 기동을 중단하도록 설정
    @Bean(name = "kafkaWorkerManager")
    public KafkaWorkerManager kafkaWorkerManager(KafkaOrderMessageQueue kafkaOrderMessageQueue, KafkaOrderProducer kafkaOrderProducer) {
        KafkaWorkerManager kafkaWorkerManager = new KafkaWorkerManager(kafkaOrderMessageQueue, kafkaOrderProducer);
        kafkaWorkerManager.start();
        if (!kafkaWorkerManager.isHealthy()) {
            throw new IllegalStateException("Kafka worker thread failed to start. Aborting.");
        }
        return kafkaWorkerManager;
    }
}

package com.learn.micro.resourceservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void publish(String resourceId) {
        log.info("Inside ResourceProducer: Sending resourceId {} to Kafka topic {}", resourceId, topic);
        kafkaTemplate.send(topic, resourceId);
    }
}

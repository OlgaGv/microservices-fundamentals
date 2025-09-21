package com.learn.micro.resourceservice.kafka;

import com.learn.micro.resourceservice.kafka.event.ResourceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceProducer {

    private final KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void publish(ResourceEvent event) {
        log.info("Inside ResourceProducer: Sending resourceId {} to Kafka topic {}", event.resourceId(), topic);
        kafkaTemplate.send(topic, event.resourceId(), event);
    }
}

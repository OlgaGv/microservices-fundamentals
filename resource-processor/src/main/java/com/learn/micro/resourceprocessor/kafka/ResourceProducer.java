package com.learn.micro.resourceprocessor.kafka;

import com.learn.micro.resourceprocessor.kafka.event.EventType;
import com.learn.micro.resourceprocessor.kafka.event.ResourceEvent;
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

    @Value("${spring.kafka.resource-processor-topic}")
    private String processorTopic;

    public void publishProcessingComplete(String resourceId) {
        ResourceEvent event = new ResourceEvent(resourceId, EventType.CREATE);
        log.info("Publishing processing complete event for resourceId={} to topic={}", resourceId, processorTopic);
        kafkaTemplate.send(processorTopic, resourceId, event);
    }
}
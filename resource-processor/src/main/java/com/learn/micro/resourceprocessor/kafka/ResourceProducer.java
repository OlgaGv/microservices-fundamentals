package com.learn.micro.resourceprocessor.kafka;

import com.learn.micro.resourceprocessor.kafka.event.EventType;
import com.learn.micro.resourceprocessor.kafka.event.ResourceEvent;
import com.learn.micro.resourceprocessor.logging.TraceContext;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
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
        String traceId = TraceContext.getTraceId();
        log.info("Publishing processing complete event for resourceId={} to topic={}, traceId={}",
            resourceId, processorTopic, traceId);
        ProducerRecord<String, ResourceEvent> producerRecord = new ProducerRecord<>(processorTopic, resourceId, event);
        if (traceId != null) {
            producerRecord.headers().add(new RecordHeader("X-Trace-Id", traceId.getBytes(StandardCharsets.UTF_8)));
        }
        kafkaTemplate.send(producerRecord);
    }
}
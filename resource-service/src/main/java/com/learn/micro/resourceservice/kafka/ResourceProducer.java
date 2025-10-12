package com.learn.micro.resourceservice.kafka;

import com.learn.micro.resourceservice.kafka.event.ResourceEvent;
import com.learn.micro.resourceservice.logging.TraceContext;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
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
        String traceId = TraceContext.getTraceId();
        log.info("Inside ResourceProducer: Sending resourceId {} to Kafka topic {}", event.resourceId(), topic);
        ProducerRecord<String, ResourceEvent> producerRecord = new ProducerRecord<>(topic, event.resourceId(), event);
        if (traceId != null) {
            producerRecord.headers().add(new org.apache.kafka.common.header.internals.RecordHeader(
                "X-Trace-Id", traceId.getBytes(StandardCharsets.UTF_8)));
        }
        kafkaTemplate.send(producerRecord);
    }
}

package com.learn.micro.resourceservice.kafka;

import com.learn.micro.resourceservice.client.StorageClient;
import com.learn.micro.resourceservice.entity.ResourceEntity;
import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.kafka.event.EventType;
import com.learn.micro.resourceservice.kafka.event.ResourceEvent;
import com.learn.micro.resourceservice.logging.TraceContext;
import com.learn.micro.resourceservice.model.Storage;
import com.learn.micro.resourceservice.repository.ResourceRepository;
import com.learn.micro.resourceservice.service.MessageHelper;
import com.learn.micro.resourceservice.service.S3Service;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceConsumer {

    private static final String STAGING_STORAGE = "STAGING";
    private static final String PERMANENT_STORAGE = "PERMANENT";
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final StorageClient storageClient;
    private final MessageHelper messageHelper;

    @KafkaListener(topics = "${app.kafka.processor-topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleResourceProcessed(ResourceEvent event, @Header(value = "X-Trace-Id", required = false) String traceId) {
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
            log.info("Generated new traceId for Kafka message: {}", traceId);
        } else {
            log.info("Using traceId from upstream Kafka message: {}", traceId);
        }
        TraceContext.setTraceId(traceId);
        MDC.put("traceId", traceId);
        log.info("Received event from processor: {}", event);
        if (event.eventType() != EventType.CREATE) {
            log.info("Ignoring event type: {}", event.eventType());
            return;
        }
        try {
            int resourceId = Integer.parseInt(event.resourceId());
            ResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalStateException("Resource not found: " + resourceId));
            if (!STAGING_STORAGE.equals(resource.getStage())) {
                log.warn("Resource {} is not in STAGING state, skipping move.", resourceId);
                return;
            }
            Storage permanentStorage = storageClient.fetchStorage(PERMANENT_STORAGE);
            String newLocation = s3Service.moveFile(resource.getS3Location(), permanentStorage);
            resource.setS3Location(newLocation);
            resource.setStage(permanentStorage.storageType());
            resourceRepository.save(resource);
            log.info("Resource {} successfully moved to PERMANENT storage.", resourceId);
        } catch (Exception e) {
            log.error("Failed to process ResourceEvent: {}", event, e);
            throw new GeneralFailureException(messageHelper.getMessage("server.error.general"));
        } finally {
            MDC.remove("traceId");
            TraceContext.clear();
        }
    }
}
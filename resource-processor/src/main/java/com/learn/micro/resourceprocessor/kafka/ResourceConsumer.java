package com.learn.micro.resourceprocessor.kafka;

import com.learn.micro.resourceprocessor.client.ResourceClient;
import com.learn.micro.resourceprocessor.client.SongClient;
import com.learn.micro.resourceprocessor.kafka.event.ResourceEvent;
import com.learn.micro.resourceprocessor.model.MetadataDto;
import com.learn.micro.resourceprocessor.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceConsumer {

    private final MetadataService metadataService;
    private final ResourceClient resourceClient;
    private final SongClient songClient;

    @KafkaListener(
        id = "resourceEventListener",
        topics = "${spring.kafka.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory")
    public void consume(ResourceEvent resourceEvent) {
        log.info("Inside ResourceConsumer: Received ResourceEvent with resourceId={}, type={}",
            resourceEvent.resourceId(), resourceEvent.eventType());
        try {
            switch (resourceEvent.eventType()) {
                case CREATE -> processCreateResource(resourceEvent);
                case DELETE -> processDeleteResource(resourceEvent);
                default ->
                    log.warn("Received event with unknown type {}", resourceEvent.eventType());
            }
        } catch (Exception e) {
            log.error("Failed to process event={} with resourceId={}: {}",
                resourceEvent.eventType(), resourceEvent.resourceId(), e.getMessage(), e);
//            throw new GeneralFailureException(e);
        }
    }

    public void processCreateResource(ResourceEvent resourceEvent) {
        log.info("Inside ResourceConsumer: perform sync call to resource client");
        byte[] fileContent = resourceClient.fetchResource(resourceEvent.resourceId());
        log.info("Fetched resourceId={} with payload size={} bytes", resourceEvent.resourceId(),
            fileContent != null ? fileContent.length : 0);
        if (!metadataService.isValidMp3(fileContent)) {
            log.warn("Invalid MP3 received for resource {}", resourceEvent.resourceId());
            return;
        }
        MetadataDto metadata = metadataService.extractMetadata(fileContent);
        metadata.setId(Integer.valueOf(resourceEvent.resourceId()));
        log.info("Extracted metadata for resourceId={}: {}", resourceEvent.resourceId(), metadata);
        log.info("Inside ResourceConsumer: perform sync call to song client");
        songClient.saveSongMetadata(metadata);
        log.info("Saved metadata for resourceId={} to SongService", resourceEvent.resourceId());
    }

    public void processDeleteResource(ResourceEvent resourceEvent) {
        log.info("Inside ResourceConsumer: perform sync call to song client");
        songClient.deleteSongMetadata(Integer.valueOf(resourceEvent.resourceId()));
        log.info("Saved metadata for resourceId={} to SongService", resourceEvent.resourceId());
    }

//    @KafkaListener(topics = "${spring.kafka.topic}",
//        groupId = "${spring.kafka.consumer.group-id}",
//        containerFactory = "kafkaListenerContainerFactory")
//    public void consume(ConsumerRecord<String, String> consumerRecord) {
//        String resourceId = consumerRecord.value();
//        log.info("Received resourceId {}", resourceId);
//        throw new GeneralFailureException("Simulate failure");
//    }
}
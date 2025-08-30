package com.learn.micro.resourceprocessor.kafka;

import com.learn.micro.resourceprocessor.client.ResourceClient;
import com.learn.micro.resourceprocessor.client.SongClient;
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

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory")
    public void consume(String resourceId) {
        log.info("Inside ResourceConsumer: Received resourceId {} for processing", resourceId);
        try {
            log.info("Inside ResourceConsumer: perform sync call to resource client");
            byte[] fileContent = resourceClient.fetchResource(resourceId);
            if (!metadataService.isValidMp3(fileContent)) {
                log.warn("Invalid MP3 received for resource {}", resourceId);
                return;
            }
            MetadataDto metadata = metadataService.extractMetadata(fileContent);
            metadata.setId(Integer.valueOf(resourceId));
            log.info("Inside ResourceConsumer: perform sync call to song client");
            songClient.saveSongMetadata(metadata);
        } catch (Exception e) {
            log.error("Failed to process resource {}: {}", resourceId, e.getMessage(), e);
        }
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
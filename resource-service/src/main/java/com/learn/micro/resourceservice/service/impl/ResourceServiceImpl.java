package com.learn.micro.resourceservice.service.impl;

import com.learn.micro.resourceservice.client.StorageClient;
import com.learn.micro.resourceservice.entity.ResourceEntity;
import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.kafka.ResourceProducer;
import com.learn.micro.resourceservice.kafka.event.EventType;
import com.learn.micro.resourceservice.kafka.event.ResourceEvent;
import com.learn.micro.resourceservice.mapper.ResourceMapper;
import com.learn.micro.resourceservice.model.DeleteResourceResponse;
import com.learn.micro.resourceservice.model.GetResourceResponse;
import com.learn.micro.resourceservice.model.Storage;
import com.learn.micro.resourceservice.model.UploadResourceResponse;
import com.learn.micro.resourceservice.repository.ResourceRepository;
import com.learn.micro.resourceservice.service.MessageHelper;
import com.learn.micro.resourceservice.service.ResourceService;
import com.learn.micro.resourceservice.service.S3Service;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResourceServiceImpl implements ResourceService {

    private static final String STAGING_STORAGE = "STAGING";
    private static final String PERMANENT_STORAGE = "PERMANENT";
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceProducer resourceProducer;
    private final MessageHelper messageHelper;
    private final S3Service s3Service;
    private final StorageClient storageClient;

    @Override
    @Transactional
    public UploadResourceResponse save(byte[] fileContent) {
        if (!isValidMp3(fileContent)) {
            throw new IllegalArgumentException(messageHelper.getMessage("validation.mp3.invalid"));
        }
        ResourceEntity savedResource;
        try {
            Storage stagingStorage = storageClient.fetchStorage(STAGING_STORAGE);
            String fullPath = s3Service.uploadMp3File(fileContent, stagingStorage);
            ResourceEntity resourceEntity = new ResourceEntity();
            resourceEntity.setS3Location(fullPath);
            resourceEntity.setStage(stagingStorage.storageType());
            savedResource = resourceRepository.save(resourceEntity);
            ResourceEvent event = new ResourceEvent(String.valueOf(savedResource.getId()), EventType.CREATE);
            resourceProducer.publish(event);
            log.info("File uploaded to STAGING and event published for resourceId={}", savedResource.getId());
        } catch (Exception e) {
            log.error("Error while saving resource", e);
            throw new GeneralFailureException(messageHelper.getMessage("server.error.general"));
        }
        return resourceMapper.mapEntityToUploadResourceDto(savedResource);
    }

    @Override
    public GetResourceResponse findById(String id) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException(
                MessageFormat.format(messageHelper.getMessage("validation.id.invalid"), id));
        }
        int resourceId = Integer.parseInt(id);
        String s3Location;
        try {
            s3Location = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format
                    (messageHelper.getMessage("error.resource.not.found"), id))).getS3Location();
        } catch (Exception e) {
            throw new GeneralFailureException("Failed to download MP3 from S3: ", e);
        }
        Storage storage = storageClient.fetchStorage(STAGING_STORAGE);
        return new GetResourceResponse(s3Service.downloadMp3File(s3Location, storage));
    }

    @Override
    @Transactional
    public DeleteResourceResponse delete(String ids) {
        List<Integer> idsToDelete = parseAndValidateIds(ids);
        List<Integer> deletedIds = new ArrayList<>();
        List<ResourceEntity> filesToDelete = resourceRepository.findAllByIdIn(idsToDelete);
        for (ResourceEntity entityToDelete : filesToDelete) {
                try {
                    resourceRepository.deleteById(entityToDelete.getId());
                    Storage storage = storageClient.fetchStorage(STAGING_STORAGE);
                    s3Service.deleteMp3File(entityToDelete.getS3Location(), storage);
                    ResourceEvent event = new ResourceEvent(String.valueOf(entityToDelete.getId()), EventType.DELETE);
                    resourceProducer.publish(event);
                    deletedIds.add(entityToDelete.getId());
                } catch (Exception e) {
                    log.error("Failed to delete file with ID: {}", entityToDelete.getId(), e);
                }
        }
        return new DeleteResourceResponse(deletedIds);
    }

    private void validateIds(String ids) {
        if (ids.length() >= 200) {
            throw new IllegalArgumentException(
                MessageFormat.format(messageHelper.getMessage("validation.ids.length"), ids.length()));
        }
        if (!ids.matches("^\\d+(,\\d+)*$")) {
            throw new IllegalArgumentException(
                    messageHelper.getMessage("validation.ids.invalid"));
        }
    }

    private boolean isValidId(String id) {
        return id.matches("^[1-9]\\d*$"); // Only allows numeric IDs
    }

    private List<Integer> parseAndValidateIds(String idParam) {
        validateIds(idParam);
        return Arrays.stream(idParam.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .map(Integer::parseInt)
                .filter(id -> id > 0)
                .toList();
    }

    private boolean isValidMp3(byte[] fileContent) {
        if (fileContent == null || fileContent.length == 0) {
            return false;
        }
        // Check for "ID3" tag at the start (common in MP3 files)
        return fileContent[0] == 'I' && fileContent[1] == 'D' && fileContent[2] == '3';
    }
}

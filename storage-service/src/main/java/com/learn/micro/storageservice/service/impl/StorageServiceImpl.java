package com.learn.micro.storageservice.service.impl;

import com.learn.micro.storageservice.entity.StorageEntity;
import com.learn.micro.storageservice.mapper.StorageMapper;
import com.learn.micro.storageservice.model.CreateStorageRequest;
import com.learn.micro.storageservice.model.CreateStorageResponse;
import com.learn.micro.storageservice.model.StorageResponse;
import com.learn.micro.storageservice.repository.StorageRepository;
import com.learn.micro.storageservice.service.StorageService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    @Override
    public CreateStorageResponse createStorage(CreateStorageRequest request) {
        log.info("Try to create storage: {}", request);
        validateCreateRequest(request);
        if (storageRepository.findByStorageType(request.storageType()).isPresent()) {
            throw new IllegalArgumentException("Storage type already exists");
        }
        StorageEntity entity = storageMapper.mapCreateRequestToEntity(request);
        StorageEntity saved = storageRepository.save(entity);
        return storageMapper.mapEntityToCreateStorageDto(saved);
    }

    @Override
    public List<StorageResponse> getAllStorages() {
        log.info("Getting all storages");
        return storageRepository.findAll().stream()
            .map(storageMapper::mapEntityToResponse)
            .toList();
    }

    @Override
    public List<Long> deleteStorages(List<Long> ids) {
        log.info("Deleting storages with ids: {}", ids);
        List<Long> deletedIds = new ArrayList<>();
        for (Long id : ids) {
            if (storageRepository.existsById(id)) {
                storageRepository.deleteById(id);
                deletedIds.add(id);
            }
        }
        return deletedIds;
    }

    @Override
    public StorageResponse getStorageByType(String type) {
        log.info("Getting storage by type: {}", type);
        return storageRepository.findByStorageType(type).map(storageMapper::mapEntityToResponse)
            .orElseThrow(() -> new IllegalArgumentException("Storage type not found: " + type));
    }

    private void validateCreateRequest(CreateStorageRequest request) {
        if (request.storageType() == null || request.storageType().isBlank()) {
            throw new IllegalArgumentException("storageType must not be null or blank");
        }
        if (request.bucket() == null || request.bucket().isBlank()) {
            throw new IllegalArgumentException("bucket must not be null or blank");
        }
        if (request.path() == null || request.path().isBlank()) {
            throw new IllegalArgumentException("path must not be null or blank");
        }
    }
}

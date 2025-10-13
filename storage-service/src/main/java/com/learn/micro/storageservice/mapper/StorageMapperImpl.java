package com.learn.micro.storageservice.mapper;

import com.learn.micro.storageservice.entity.StorageEntity;
import com.learn.micro.storageservice.model.CreateStorageRequest;
import com.learn.micro.storageservice.model.CreateStorageResponse;
import com.learn.micro.storageservice.model.StorageResponse;

public class StorageMapperImpl implements StorageMapper {

    @Override
    public StorageEntity mapCreateRequestToEntity(CreateStorageRequest request) {
        return StorageEntity.builder()
            .storageType(request.storageType())
            .bucket(request.bucket())
            .path(request.path())
            .build();
    }

    @Override
    public CreateStorageResponse mapEntityToCreateStorageDto(StorageEntity entity) {
        return new CreateStorageResponse(entity.getId());
    }

    @Override
    public StorageResponse mapEntityToResponse(StorageEntity entity) {
        return new StorageResponse(entity.getId(), entity.getStorageType(), entity.getBucket(), entity.getPath());
    }
}
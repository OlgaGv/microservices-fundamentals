package com.learn.micro.storageservice.mapper;

import com.learn.micro.storageservice.entity.StorageEntity;
import com.learn.micro.storageservice.model.CreateStorageRequest;
import com.learn.micro.storageservice.model.CreateStorageResponse;
import com.learn.micro.storageservice.model.StorageResponse;
import org.springframework.stereotype.Component;

@Component
public interface StorageMapper {

    StorageEntity mapCreateRequestToEntity(CreateStorageRequest request);

    CreateStorageResponse mapEntityToCreateStorageDto(StorageEntity entity);

    StorageResponse mapEntityToResponse(StorageEntity entity);
}

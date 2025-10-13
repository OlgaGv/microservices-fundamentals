package com.learn.micro.storageservice.service;

import com.learn.micro.storageservice.model.CreateStorageRequest;
import com.learn.micro.storageservice.model.CreateStorageResponse;
import com.learn.micro.storageservice.model.StorageResponse;
import java.util.List;

public interface StorageService {

    CreateStorageResponse createStorage(CreateStorageRequest request);
    List<StorageResponse> getAllStorages();
    List<Long> deleteStorages(List<Long> ids);
    StorageResponse getStorageByType(String type);
}
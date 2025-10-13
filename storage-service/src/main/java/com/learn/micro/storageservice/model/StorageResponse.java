package com.learn.micro.storageservice.model;

public record StorageResponse(
        Long id,
        String storageType,
        String bucket,
        String path
) {}
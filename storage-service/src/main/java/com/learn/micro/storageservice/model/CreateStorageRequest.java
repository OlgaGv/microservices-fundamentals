package com.learn.micro.storageservice.model;

public record CreateStorageRequest(
        String storageType,
        String bucket,
        String path
) {}
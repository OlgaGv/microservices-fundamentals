package com.learn.micro.resourceservice.model;

import lombok.Builder;

@Builder
public record Storage (String storageType, String bucket, String path) {
}
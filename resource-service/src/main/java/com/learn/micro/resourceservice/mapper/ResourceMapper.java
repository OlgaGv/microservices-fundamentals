package com.learn.micro.resourceservice.mapper;

import com.learn.micro.resourceservice.entity.ResourceEntity;
import com.learn.micro.resourceservice.model.UploadResourceResponse;

public interface ResourceMapper {

    UploadResourceResponse mapEntityToUploadResourceDto(ResourceEntity from);
}

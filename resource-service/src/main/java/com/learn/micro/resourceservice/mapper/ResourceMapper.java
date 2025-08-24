package com.learn.micro.resourceservice.mapper;

import com.learn.micro.resourceservice.entity.ResourceEntity;
import com.learn.micro.resourceservice.model.GetResourceResponse;
import com.learn.micro.resourceservice.model.UploadResourceResponse;

public interface ResourceMapper {

    GetResourceResponse mapEntityToResourceContentDto(ResourceEntity from);

    UploadResourceResponse mapEntityToUploadResourceDto(ResourceEntity from);
}

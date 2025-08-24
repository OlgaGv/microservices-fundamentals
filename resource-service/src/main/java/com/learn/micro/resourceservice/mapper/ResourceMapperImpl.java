package com.learn.micro.resourceservice.mapper;

import org.springframework.stereotype.Component;

import com.learn.micro.resourceservice.entity.ResourceEntity;
import com.learn.micro.resourceservice.model.UploadResourceResponse;

@Component
public class ResourceMapperImpl implements ResourceMapper {

    @Override
    public UploadResourceResponse mapEntityToUploadResourceDto(ResourceEntity from) {
        return new UploadResourceResponse(from.getId(), from.getS3Location());
    }
}

package com.learn.micro.resourceservice.service;

import com.learn.micro.resourceservice.model.DeleteResourceResponse;
import com.learn.micro.resourceservice.model.GetResourceResponse;
import com.learn.micro.resourceservice.model.UploadResourceResponse;

public interface ResourceService {

    GetResourceResponse findById(String id);

    UploadResourceResponse save(byte[] fileContent);

    DeleteResourceResponse delete(String id);
}

package com.learn.micro.resourceservice.service;

import com.learn.micro.resourceservice.model.Storage;

public interface S3Service {

    String uploadMp3File(byte[] fileContent, Storage storage);

    byte[] downloadMp3File(String path, Storage storage);

    void deleteMp3File(String path, Storage storage);

    String moveFile(String currentLocation, Storage targetStorage);
}

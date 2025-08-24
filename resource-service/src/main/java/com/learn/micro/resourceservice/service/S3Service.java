package com.learn.micro.resourceservice.service;

public interface S3Service {

    String uploadMp3File(byte[] fileContent);

    byte[] downloadMp3File(String path);

    void deleteMp3File(String path);
}

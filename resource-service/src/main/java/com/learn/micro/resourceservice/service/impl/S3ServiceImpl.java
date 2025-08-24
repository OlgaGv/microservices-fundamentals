package com.learn.micro.resourceservice.service.impl;

import com.learn.micro.resourceservice.configutarion.AwsProperties;
import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.service.S3Service;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    @Value("${aws.s3-properties.bucket}")
    private String bucketName;

    public String uploadMp3File(byte[] fileContent) {
        String s3Key = UUID.randomUUID() + ".mp3";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("audio/mpeg")
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(fileContent));
        return "s3://" + bucketName + "/" + s3Key;
    }

    public byte[] downloadMp3File(String s3Location) {
        String s3Key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build();
        try (InputStream inputStream = s3Client.getObject(request);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new GeneralFailureException("Failed to download MP3 from S3", e);
        }
    }

    public void deleteMp3File(String s3Location) {
        String s3Key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(s3Key));
        } catch (S3Exception e) {
            log.error("Error deleting object: {}", e.awsErrorDetails().errorMessage());
            throw new GeneralFailureException("Error deleting object from S3: " + e.awsErrorDetails().errorMessage());
        }
    }
}

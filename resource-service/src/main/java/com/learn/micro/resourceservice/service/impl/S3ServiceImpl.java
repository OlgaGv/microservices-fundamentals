package com.learn.micro.resourceservice.service.impl;

import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.service.S3Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
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

    @Value("${aws.s3-properties.bucket}")
    private String bucketName;

    private final RetryTemplate retryTemplate = RetryTemplate.builder()
        .maxAttempts(3)
        .fixedBackoff(2000)
        .retryOn(S3Exception.class)
        .retryOn(IOException.class)
        .build();

    public String uploadMp3File(byte[] fileContent) {
        return retryTemplate.execute(context -> {
            String s3Key = UUID.randomUUID() + ".mp3";
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("audio/mpeg")
                .build();
            s3Client.putObject(request, RequestBody.fromBytes(fileContent));
            log.info("Uploaded file to S3: {}", s3Key);
            return "s3://" + bucketName + "/" + s3Key;
        }, context -> {
            int totalAttempts = context.getRetryCount() + 1;
            log.error("Failed to upload file to S3 after {} attempts", totalAttempts);
            throw new GeneralFailureException("Failed to upload file to S3 after retries");
        });
    }

    public byte[] downloadMp3File(String s3Location) {
        return retryTemplate.execute(context -> {
            String s3Key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
            try (InputStream inputStream = s3Client.getObject(request);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                inputStream.transferTo(outputStream);
                log.info("Downloaded file from S3: {}", s3Key);
                return outputStream.toByteArray();
            } catch (IOException e) {
                throw new GeneralFailureException("Failed to read object from S3", e);
            }
        }, context -> {
            log.error("Failed to download file from S3 after {} attempts", context.getRetryCount());
            throw new GeneralFailureException("Failed to download file from S3 after retries");
        });
    }

    public void deleteMp3File(String s3Location) {
        retryTemplate.execute(context -> {
            String s3Key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(s3Key));
            log.info("Deleted file from S3: {}", s3Key);
            return null;
        }, context -> {
            log.error("Failed to delete file from S3 after {} attempts", context.getRetryCount());
            throw new GeneralFailureException("Failed to delete file from S3 after retries");
        });
    }
}

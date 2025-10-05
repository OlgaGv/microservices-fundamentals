package com.learn.micro.resourceservice.service.impl;

import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.model.Storage;
import com.learn.micro.resourceservice.service.S3Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    public static final String PREFIX = "s3://";
    private final S3Client s3Client;

    private final RetryTemplate retryTemplate = RetryTemplate.builder()
        .maxAttempts(3)
        .fixedBackoff(2000)
        .retryOn(S3Exception.class)
        .retryOn(IOException.class)
        .build();

    public String uploadMp3File(byte[] fileContent, Storage storage) {
        return retryTemplate.execute(context -> {
            String s3Key = storage.path() + "/" + UUID.randomUUID() + ".mp3";
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(storage.bucket())
                .key(s3Key)
                .contentType("audio/mpeg")
                .build();
            s3Client.putObject(request, RequestBody.fromBytes(fileContent));
            log.info("Uploaded file to S3: {}", s3Key);
            return PREFIX + storage.bucket() + s3Key;
        }, context -> {
            int totalAttempts = context.getRetryCount() + 1;
            log.error("Failed to upload file to S3 after {} attempts", totalAttempts);
            throw new GeneralFailureException("Failed to upload file to S3 after retries");
        });
    }

    public byte[] downloadMp3File(String s3Location, Storage storage) {
        return retryTemplate.execute(context -> {
            if (!s3Location.startsWith(PREFIX)) {
                throw new IllegalArgumentException("Invalid S3 URI: " + s3Location);
            }
            log.info("STORAGE: {} {}", storage.bucket(), storage.path());
            log.info("S3LOCATION: {}", s3Location);
            String withoutPrefix = s3Location.substring(5);

            int firstSlash = withoutPrefix.indexOf('/');
            if (firstSlash == -1) {
                throw new IllegalArgumentException("Invalid S3 URI format: " + s3Location);
            }
            String bucket = withoutPrefix.substring(0, firstSlash);
            log.info("BUCKET: {}", bucket);
            String s3Key = withoutPrefix.substring(firstSlash);
            log.info("KEY: {}", s3Key);
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();
            try (InputStream inputStream = s3Client.getObject(request);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                inputStream.transferTo(outputStream);
                log.info("Downloaded file from S3: {}/{}", bucket, s3Key);
                return outputStream.toByteArray();
            } catch (IOException e) {
                throw new GeneralFailureException("Failed to read object from S3", e);
            }
        }, context -> {
            log.error("Failed to download file from S3 after {} attempts", context.getRetryCount() + 1);
            throw new GeneralFailureException("Failed to download file from S3 after retries");
        });
    }

    public void deleteMp3File(String s3Location, Storage storage) {
        retryTemplate.execute(context -> {
            if (!s3Location.startsWith(PREFIX)) {
                throw new IllegalArgumentException("Invalid S3 URI: " + s3Location);
            }
            String withoutPrefix = s3Location.substring(5);
            int firstSlash = withoutPrefix.indexOf('/');
            if (firstSlash == -1) {
                throw new IllegalArgumentException("Invalid S3 URI format: " + s3Location);
            }
            String bucket = withoutPrefix.substring(0, firstSlash);
            String s3Key = withoutPrefix.substring(firstSlash);
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(s3Key));
            log.info("Deleted file from S3: {}", s3Key);
            return null;
        }, context -> {
            log.error("Failed to delete file from S3 after {} attempts", context.getRetryCount());
            throw new GeneralFailureException("Failed to delete file from S3 after retries");
        });
    }

    @Override
    public String moveFile(String currentLocation, Storage targetStorage) {
        return retryTemplate.execute(context -> {
            try {
                String[] parts = currentLocation.replace(PREFIX, "").split("/", 2);
                if (parts.length < 2) {
                    throw new IllegalArgumentException("Invalid S3 location: " + currentLocation);
                }
                String sourceBucket = parts[0];
                log.info("BUCKET: {}", sourceBucket);
                String sourceKey = "/" + parts[1];
                log.info("KEY: {}", sourceKey);
                GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(sourceBucket)
                    .key(sourceKey)
                    .build();
                byte[] fileBytes;
                try (InputStream inputStream = s3Client.getObject(getRequest);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    inputStream.transferTo(outputStream);
                    fileBytes = outputStream.toByteArray();
                }
                String newKey = targetStorage.path() + "/" + UUID.randomUUID() + ".mp3";
                PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(targetStorage.bucket())
                    .key(newKey)
                    .contentType("audio/mpeg")
                    .build();
                s3Client.putObject(putRequest, RequestBody.fromBytes(fileBytes));
                log.info("Moved file to new bucket: {}, key: {}", targetStorage.bucket(), newKey);
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(sourceBucket)
                    .key(sourceKey)
                    .build();
                s3Client.deleteObject(deleteRequest);
                log.info("Deleted old file from S3: {}/{}", sourceBucket, sourceKey);
                return PREFIX + targetStorage.bucket() + newKey;
            } catch (Exception e) {
                log.error("Error while moving file from {} to {}", currentLocation, targetStorage.bucket(), e);
                throw new GeneralFailureException("Failed to move file between storages", e);
            }
        }, context -> {
            log.error("Failed to move file after {} attempts", context.getRetryCount() + 1);
            throw new GeneralFailureException("Failed to move file after retries");
        });
    }
}

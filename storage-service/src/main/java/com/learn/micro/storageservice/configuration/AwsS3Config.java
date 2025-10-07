package com.learn.micro.storageservice.configuration;

import com.learn.micro.storageservice.entity.Storage;
import com.learn.micro.storageservice.repository.StorageRepository;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Configuration
public class AwsS3Config {

    StorageRepository storageRepository;

    @Bean
    public S3Client s3Client(AwsProperties awsProperties) {
        return S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .endpointOverride(URI.create(awsProperties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        awsProperties.getCredentials().getAccessKey(),
                        awsProperties.getCredentials().getSecretKey())))
                .serviceConfiguration(
                        S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Bean
    public CommandLineRunner initStorages(StorageRepository repository, S3Client s3Client) {
        return args -> {
            List<Storage> storages = repository.findAll();
            if (storages.isEmpty()) {
                log.warn("No storage records found in database. Skipping bucket creation.");
                return;
            }
            storages.forEach(storage -> {
                String bucketName = storage.getBucket();
                String path = storage.getPath();
                try {
                    s3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
                    log.info("Created bucket: {} (path: {})", bucketName, path);
                } catch (S3Exception e) {
                    if ("BucketAlreadyOwnedByYou".equals(e.awsErrorDetails().errorCode()) ||
                        "BucketAlreadyExists".equals(e.awsErrorDetails().errorCode())) {
                        log.info("Bucket {} already exists. Skipping creation.", bucketName);
                    } else {
                        log.error("Error creating bucket {}: {}", bucketName, e.awsErrorDetails().errorMessage());
                    }
                }
            });
        };
    }
}
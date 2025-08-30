package com.learn.micro.resourceservice.configutarion;

import java.net.URI;

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
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Configuration
public class AwsS3Config {

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
    public CommandLineRunner createBucketIfNotExists(S3Client s3Client, AwsProperties awsProperties) {
        return args -> {
            String bucketName = awsProperties.getS3Properties().getBucket();
            try {
                s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
                log.info("S3 bucket: {} already exists", bucketName);
            } catch (S3Exception e) {
                try {
                    s3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
                    log.info("S3 bucket: {} was created", bucketName);
                } catch (S3Exception ce) {
                    log.error("Failed to create S3 bucket: {} with an error: {}", bucketName, ce.awsErrorDetails().errorMessage(), ce);
                    throw ce;
                }
            }
        };
    }
}

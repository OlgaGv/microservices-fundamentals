package com.learn.micro.resourceservice.configutarion;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Slf4j
@Configuration
@Profile("!test")
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
}

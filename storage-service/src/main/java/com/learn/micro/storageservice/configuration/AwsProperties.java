package com.learn.micro.storageservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="aws")
public class AwsProperties {

    private String region;
    private String endpoint;
    private S3Properties s3Properties;
    private Credentials credentials;

    @Getter
    @Setter
    public static class S3Properties {
        private String bucket;
    }

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }
}

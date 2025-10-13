package com.learn.micro.storageservice;

import com.learn.micro.storageservice.configuration.AwsProperties;
import com.learn.micro.storageservice.configuration.AwsS3Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AwsS3Config.class)
@EnableConfigurationProperties(AwsProperties.class)
public class StorageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApplication.class, args);
    }

}

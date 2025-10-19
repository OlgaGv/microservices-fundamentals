package com.learn.micro.storageservice.configuration;

import brave.http.HttpRequest;
import brave.sampler.SamplerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    public SamplerFunction<HttpRequest> httpServerSampler() {
        return request -> {
            String url = request.url();
            if (url.contains("eureka")) {
                return false;
            }
            return null;
        };
    }
}
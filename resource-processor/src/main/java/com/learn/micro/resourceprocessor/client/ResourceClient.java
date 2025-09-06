package com.learn.micro.resourceprocessor.client;

import com.learn.micro.resourceprocessor.exception.GeneralFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceClient {

    private final RestTemplate restTemplate;

    @Value("${services.resource-service.url}")
    private String resourceServiceUrl;

    @Retryable(
        retryFor = {HttpServerErrorException.class, ResourceAccessException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public byte[] fetchResource(String resourceId) {
        String url = resourceServiceUrl + "/resources/" + resourceId;
        int attempt = RetrySynchronizationManager.getContext() != null
            ? RetrySynchronizationManager.getContext().getRetryCount() + 1
            : 1;
        log.info("Inside ResourceClient: Attempt {} to get resource by Id: {}", attempt,
            resourceId);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new IllegalStateException(
            "Failed to fetch resource from Resource Service: " + resourceId);
    }

    @Recover
    public byte[] recover(Exception e, String resourceId) {
        throw new GeneralFailureException("Failed to fetch resource after retries: " + resourceId,
            e);
    }
}

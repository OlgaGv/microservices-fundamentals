package com.learn.micro.resourceprocessor.client;

import com.learn.micro.resourceprocessor.exception.GeneralFailureException;
import com.learn.micro.resourceprocessor.model.MetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class SongClient {

    private final RestTemplate restTemplate;
    @Value("${services.song-service.url}")
    private String songServiceUrl;

    @Retryable(
        retryFor = {HttpServerErrorException.class, ResourceAccessException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void saveSongMetadata(MetadataDto metadata) {
        String url = songServiceUrl + "/songs";
        int attempt = RetrySynchronizationManager.getContext() != null
            ? RetrySynchronizationManager.getContext().getRetryCount() + 1
            : 1;
        log.info("Inside SongClient: Attempt {} to save metadata for song: {}", attempt,
            metadata.getName());
        restTemplate.postForEntity(url, metadata, Void.class);
    }

    @Recover
    public void recover(Exception e, MetadataDto metadata) {
        throw new GeneralFailureException("Failed to save song metadata: " + metadata.getName(), e);
    }
}

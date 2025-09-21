package com.learn.micro.resourceprocessor.client;

import com.learn.micro.resourceprocessor.exception.GeneralFailureException;
import com.learn.micro.resourceprocessor.model.MetadataDto;
import com.learn.micro.resourceprocessor.service.ServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongClient {

    private static final String SONG_SERVICE = "song-service";
    private final RestTemplate restTemplate;
    private final ServiceProvider serviceProvider;

    @Retryable(
        retryFor = {HttpServerErrorException.class, GeneralFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void saveSongMetadata(MetadataDto metadata) {
        ServiceInstance songService = serviceProvider.getServiceInstance(SONG_SERVICE);
        String url = songService.getUri() + "/songs";
        int attempt = RetrySynchronizationManager.getContext() != null
            ? RetrySynchronizationManager.getContext().getRetryCount() + 1
            : 1;
        log.info("Inside SongClient: Attempt {} to save metadata for song: {}", attempt,
            metadata.getName());
        restTemplate.postForEntity(url, metadata, Void.class);
    }

    @Retryable(
        retryFor = {HttpServerErrorException.class, GeneralFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void deleteSongMetadata(Integer songId) {
        ServiceInstance songService = serviceProvider.getServiceInstance(SONG_SERVICE);
        String url = songService.getUri() + "/songs?id={id}";
        int attempt = RetrySynchronizationManager.getContext() != null
            ? RetrySynchronizationManager.getContext().getRetryCount() + 1
            : 1;
        log.info("Inside SongClient: Attempt {} to delete metadata for songId: {}", attempt, songId);
        restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class, songId);
    }

    @Recover
    public void recover(Exception e, MetadataDto metadata) {
        throw new GeneralFailureException("Failed to save song metadata: " + metadata.getName(), e);
    }

    @Recover
    public void recover(Exception e, Integer songId) {
        throw new GeneralFailureException("Failed to delete song metadata: " + songId, e);
    }
}

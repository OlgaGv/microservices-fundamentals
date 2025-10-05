package com.learn.micro.resourceservice.client;

import com.learn.micro.resourceservice.model.Storage;
import com.learn.micro.resourceservice.service.ServiceProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageClient {

    private static final String STORAGE_SERVICE = "storage-service";
    private static final String STORAGE_CB = "storageServiceCircuitBreaker";
    private final RestTemplate restTemplate;
    private final ServiceProvider serviceProvider;

    @CircuitBreaker(name = STORAGE_CB, fallbackMethod = "fetchStorageFallback")
    public Storage fetchStorage(String storageType) {
        ServiceInstance storageService = serviceProvider.getServiceInstance(STORAGE_SERVICE);
        String url = storageService.getUri() + "/storages/type/" + storageType;
        log.info("Storage URL: {}", url);
        ResponseEntity<Storage> response =
            restTemplate.getForEntity(url, Storage.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new IllegalStateException(
            "Failed to fetch storage from Storage Service: " + storageType);
    }

    public Storage fetchStorageFallback(String storageType, Throwable throwable) {
        log.warn("Storage Service unavailable, returning stub data for type: {}. Reason: {}", storageType, throwable.getMessage());
        return Storage.builder()
            .storageType(storageType)
            .bucket(storageType.toLowerCase() + "-bucket")
            .path("/files")
            .build();
    }
}

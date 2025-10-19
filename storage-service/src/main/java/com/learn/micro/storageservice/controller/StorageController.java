package com.learn.micro.storageservice.controller;

import com.learn.micro.storageservice.model.CreateStorageRequest;
import com.learn.micro.storageservice.model.CreateStorageResponse;
import com.learn.micro.storageservice.model.StorageResponse;
import com.learn.micro.storageservice.service.impl.StorageServiceImpl;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
@Slf4j
public class StorageController {

    private final StorageServiceImpl storageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateStorageResponse> createStorage(@RequestBody CreateStorageRequest request) {
        CreateStorageResponse response = storageService.createStorage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<StorageResponse>> getAllStorages() {
        return ResponseEntity.ok(storageService.getAllStorages());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<StorageResponse> getStorageByType(@PathVariable String type) {
        return ResponseEntity.ok(storageService.getStorageByType(type));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Long>> deleteStorages(@RequestParam String id) {
        if (id.length() > 200) {
            return ResponseEntity.badRequest().build();
        }
        List<Long> ids = Arrays.stream(id.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .toList();
        List<Long> deletedIds = storageService.deleteStorages(ids);
        return ResponseEntity.ok(deletedIds);
    }
}

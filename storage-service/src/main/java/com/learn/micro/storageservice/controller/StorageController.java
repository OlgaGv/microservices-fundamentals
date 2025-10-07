package com.learn.micro.storageservice.controller;

import com.learn.micro.storageservice.entity.Storage;
import com.learn.micro.storageservice.service.impl.StorageServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Long>> createStorage(@RequestBody Storage storage) {
        Storage created = storageService.createStorage(storage);
        return ResponseEntity.ok(Map.of("id", created.getId()));
    }

    @GetMapping
    public ResponseEntity<List<Storage>> getAllStorages() {
        return ResponseEntity.ok(storageService.getAllStorages());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Storage> getStorageByType(@PathVariable String type) {
        return ResponseEntity.ok(storageService.getStorageByType(type));
    }


    @DeleteMapping
    public ResponseEntity<List<Long>> deleteStorages(@RequestParam String id) {
        if (id.length() > 200) {
            return ResponseEntity.badRequest().build();
        }
        List<Long> ids = Arrays.stream(id.split(","))
            .map(Long::parseLong)
            .toList();
        List<Long> deletedIds = storageService.deleteStorages(ids);
        return ResponseEntity.ok(deletedIds);
    }
}

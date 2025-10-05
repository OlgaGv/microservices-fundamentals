package com.learn.micro.storageservice.service.impl;

import com.learn.micro.storageservice.entity.Storage;
import com.learn.micro.storageservice.entity.StorageType;
import com.learn.micro.storageservice.repository.StorageRepository;
import com.learn.micro.storageservice.service.StorageService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    public Storage createStorage(Storage storage) {
        if (storageRepository.findByStorageType(storage.getStorageType()).isPresent()) {
            throw new IllegalArgumentException("Storage type already exists");
        }
        return storageRepository.save(storage);
    }

    public List<Storage> getAllStorages() {
        return storageRepository.findAll();
    }

    public List<Long> deleteStorages(List<Long> ids) {
        List<Long> deletedIds = new ArrayList<>();
        for (Long id : ids) {
            if (storageRepository.existsById(id)) {
                storageRepository.deleteById(id);
                deletedIds.add(id);
            }
        }
        return deletedIds;
    }

    public Storage getStorageByType(String type) {
        return storageRepository.findByStorageType(type)
            .orElseThrow(() -> new IllegalArgumentException("Storage type not found: " + type));
    }
}

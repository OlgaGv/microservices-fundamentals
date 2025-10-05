package com.learn.micro.storageservice.service;

import com.learn.micro.storageservice.entity.Storage;
import java.util.List;

public interface StorageService {

    Storage createStorage(Storage storage);
    List<Storage> getAllStorages();
    List<Long> deleteStorages(List<Long> ids);
    Storage getStorageByType(String type);
}
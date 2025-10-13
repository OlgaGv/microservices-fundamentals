package com.learn.micro.storageservice.repository;

import com.learn.micro.storageservice.entity.StorageEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<StorageEntity, Long> {

    Optional<StorageEntity> findByStorageType(String storageType);
}

package com.learn.micro.storageservice.repository;

import com.learn.micro.storageservice.entity.Storage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    Optional<Storage> findByStorageType(String storageType);
}

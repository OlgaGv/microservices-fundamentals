package com.learn.micro.resourceservice.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.micro.resourceservice.entity.ResourceEntity;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, Integer> {

    List<ResourceEntity> findAllByIdIn(List<Integer> ids);
}

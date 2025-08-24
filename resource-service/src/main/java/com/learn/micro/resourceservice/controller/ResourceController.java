package com.learn.micro.resourceservice.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.micro.resourceservice.model.DeleteResourceResponse;
import com.learn.micro.resourceservice.model.GetResourceResponse;
import com.learn.micro.resourceservice.model.UploadResourceResponse;
import com.learn.micro.resourceservice.service.ResourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> findById(@PathVariable("id") String id) {
        GetResourceResponse result = resourceService.findById(id);
        return ResponseEntity.ok().contentType(MediaType.valueOf("audio/mpeg"))
            .body(result.content());
    }

    @PostMapping
    public ResponseEntity<UploadResourceResponse> upload(@RequestBody byte[] fileContent) {
        UploadResourceResponse result = resourceService.save(fileContent);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<DeleteResourceResponse> delete(@RequestParam("id") String id) {
        DeleteResourceResponse result = resourceService.delete(id);
        return ResponseEntity.ok(result);
    }
}


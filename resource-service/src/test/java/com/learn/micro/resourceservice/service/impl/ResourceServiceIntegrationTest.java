package com.learn.micro.resourceservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.learn.micro.resourceservice.entity.ResourceEntity;
import com.learn.micro.resourceservice.kafka.ResourceProducer;
import com.learn.micro.resourceservice.model.DeleteResourceResponse;
import com.learn.micro.resourceservice.model.GetResourceResponse;
import com.learn.micro.resourceservice.model.UploadResourceResponse;
import com.learn.micro.resourceservice.repository.ResourceRepository;
import com.learn.micro.resourceservice.service.ResourceService;
import com.learn.micro.resourceservice.service.S3Service;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class ResourceServiceIntegrationTest {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceRepository resourceRepository;

    @MockitoBean
    private S3Service s3Service;

    @MockitoBean
    private ResourceProducer resourceProducer;

    @BeforeEach
    void setUp() {
        resourceRepository.deleteAll();
    }

    /**
     * Tests the behavior of uploading an MP3 file using the ResourceService. 
     * Verifies that the metadata is persisted in the database and the returned response 
     * contains correct information.
     */
    @Test
    void whenUploadMp3SaveFileAndReturnResponse() {
        // given
        byte[] mp3File = new byte[]{'I', 'D', '3', 0, 0, 0};
        String mockS3Location = "test-bucket/test-file.mp3";
        when(s3Service.uploadMp3File(mp3File)).thenReturn(mockS3Location);
        // when
        UploadResourceResponse response = resourceService.save(mp3File);
        // then
        assertNotNull(response);
        ResourceEntity savedEntity = resourceRepository.findById(response.id()).orElseThrow();
        assertEquals(mockS3Location, savedEntity.getS3Location());
        verify(s3Service).uploadMp3File(mp3File);
    }

    /**
     * Tests fetching an uploaded MP3 file by its resource ID. 
     * Verifies that the ResourceService returns the correct byte content.
     */
    @Test
    void whenFindByIdReturnFileContent() {
        // given
        byte[] mp3File = new byte[]{'I', 'D', '3', 0, 0, 0};
        String s3Location = "test-bucket/test-file.mp3";
        ResourceEntity entity = resourceRepository.save(new ResourceEntity(null, s3Location));
        when(s3Service.downloadMp3File(s3Location)).thenReturn(mp3File);
        // when
        GetResourceResponse response = resourceService.findById(entity.getId().toString());
        // then
        assertNotNull(response);
        assertArrayEquals(mp3File, response.content());
        verify(s3Service).downloadMp3File(s3Location);
    }

    /**
     * Tests deleting an MP3 resource by its ID. 
     * Verifies that the resource is removed from the database and S3.
     */
    @Test
    void whenDeleteRemoveFromDbAndS3() {
        // given
        String s3Location = "test-bucket/test-file.mp3";
        ResourceEntity entity = resourceRepository.save(new ResourceEntity(null, s3Location));
        doNothing().when(s3Service).deleteMp3File(s3Location);
        // when
        DeleteResourceResponse response = resourceService.delete(entity.getId().toString());
        // then
        assertEquals(List.of(entity.getId()), response.ids());
        assertFalse(resourceRepository.existsById(entity.getId()));
        verify(s3Service).deleteMp3File(s3Location);
    }

    /**
     * Tests handling of invalid MP3 file upload.
     */
    @Test
    void whenUploadInvalidMp3FileShouldThrowException() {
        // given
        byte[] invalidFile = new byte[]{0, 1, 2, 3};
        // when and then
        assertThrows(IllegalArgumentException.class, () -> resourceService.save(invalidFile));
        verify(s3Service, never()).uploadMp3File(any());
    }

    /**
     * Tests handling of non-existent resource retrieval.
     */
    @Test
    void whenFindByIdWithNonExistentIdShouldThrowException() {
        // given
        String nonExistentId = "999";
        // when and then
        assertThrows(Exception.class, () -> resourceService.findById(nonExistentId));
    }

    /**
     * Tests handling of non-existent resource deletion.
     */
    @Test
    void whenDeleteNonExistentResourceShouldReturnEmptyList() {
        // given
        String nonExistentId = "999";
        // when
        DeleteResourceResponse response = resourceService.delete(nonExistentId);
        // then
        assertEquals(List.of(), response.ids());
        verify(s3Service, never()).deleteMp3File(any());
    }
}

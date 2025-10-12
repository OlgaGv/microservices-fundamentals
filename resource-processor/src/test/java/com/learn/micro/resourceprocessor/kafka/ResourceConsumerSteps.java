package com.learn.micro.resourceprocessor.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.learn.micro.resourceprocessor.client.ResourceClient;
import com.learn.micro.resourceprocessor.client.SongClient;
import com.learn.micro.resourceprocessor.kafka.event.EventType;
import com.learn.micro.resourceprocessor.kafka.event.ResourceEvent;
import com.learn.micro.resourceprocessor.model.MetadataDto;
import com.learn.micro.resourceprocessor.service.MetadataService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class ResourceConsumerSteps {

    private String resourceId;
    private byte[] resourceContent;

    @Autowired
    private ResourceConsumer resourceConsumer;

    @MockitoBean
    private ResourceClient resourceClient;

    @MockitoBean
    private SongClient songClient;

    @Autowired
    private MetadataService metadataService;

    // ------------------ Scenario 1: Successfully process a valid MP3 resource ------------------
    @Given("a valid resource ID {string}")
    public void given_valid_resource_id(String id) throws IOException {
        this.resourceId = id;
        resourceContent = loadTestMp3();
        when(resourceClient.fetchResource(resourceId)).thenReturn(resourceContent);
    }

    @When("the resource processor consumes the resource ID")
    public void when_resource_processor_consumes_resource_id() {
        resourceConsumer.consume(new ResourceEvent(resourceId, EventType.CREATE), "traceId");
    }

    @Then("the resource is fetched from the resource service")
    public void then_resource_fetched_from_resource_service() {
        verify(resourceClient).fetchResource(resourceId);
    }

    @Then("the MP3 is validated")
    public void then_mp3_is_validated() {
        if (!metadataService.isValidMp3(resourceContent)) {
            throw new AssertionError("MP3 validation failed");
        }
    }

    @Then("metadata is extracted")
    public void then_metadata_is_extracted() {
        MetadataDto metadata = metadataService.extractMetadata(resourceContent);
        if (metadata == null) {
            throw new AssertionError("Metadata extraction failed");
        }
    }

    @Then("the metadata is sent to the Song service")
    public void then_metadata_sent_to_song_service() {
        verify(songClient).saveSongMetadata(argThat(m ->
            "Test Title".equals(m.getName()) &&
                "Test Artist".equals(m.getArtist()) &&
                "Test Album".equals(m.getAlbum()) &&
                "00:07".equals(m.getDuration()) &&
                "2025".equals(m.getYear())
        ));
    }

    // ------------------ Scenario 2: Ignore non-MP3 resource ------------------
    @Given("a resource ID {string} pointing to a non-MP3 file")
    public void given_non_mp3_resource_id(String id) {
        this.resourceId = id;
        resourceContent = new byte[]{0, 1, 2, 3}; // invalid MP3 content
        when(resourceClient.fetchResource(resourceId)).thenReturn(resourceContent);
    }

    @When("the resource processor consumes the non-MP3 resource ID")
    public void when_resource_processor_consumes_non_mp3() {
        resourceConsumer.consume(new ResourceEvent(resourceId, EventType.CREATE),"traceId");
    }

    @Then("the MP3 validation fails")
    public void then_mp3_validation_fails() {
        if (metadataService.isValidMp3(resourceContent)) {
            throw new AssertionError("MP3 validation should fail");
        }
    }

    @Then("no metadata is sent to the Song service")
    public void then_no_metadata_is_sent() {
        verify(songClient, never()).saveSongMetadata(any());
    }

    private byte[] loadTestMp3() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/test-data/test.mp3")) {
            if (is == null) {
                throw new IllegalStateException("Test MP3 not found at /test-data/test.mp3");
            }
            return is.readAllBytes();
        }
    }
}

package com.learn.micro.resourceprocessor.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class TestDatabaseUtils {
    
    private final RestTemplate restTemplate;
    private final String songServiceBaseUrl;
    private final ObjectMapper objectMapper;
    
    public TestDatabaseUtils(RestTemplate restTemplate, String songServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.songServiceBaseUrl = songServiceBaseUrl;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get the latest created song
     */
    public Optional<Map<String, Object>> getLatestSong() {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                songServiceBaseUrl + "/songs", 
                Object.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return getLatestSongFromResponse(response.getBody());
            }
        } catch (Exception e) {
            log.warn("Could not retrieve songs from service (this might be expected in test environment): {}", e.getMessage());
        }
        return Optional.empty();
    }
    
    @SuppressWarnings("unchecked")
    private Optional<Map<String, Object>> getLatestSongFromResponse(Object responseBody) {
        try {
            if (responseBody instanceof List) {
                List<Object> songs = (List<Object>) responseBody;
                if (!songs.isEmpty()) {
                    // Find the song with the highest ID
                    return songs.stream()
                        .map(this::convertToMap)
                        .filter(song -> song.get("id") != null)
                        .max((s1, s2) -> {
                            Integer id1 = ((Number) s1.get("id")).intValue();
                            Integer id2 = ((Number) s2.get("id")).intValue();
                            return id1.compareTo(id2);
                        });
                }
            } else if (responseBody instanceof Map) {
                return Optional.of(convertToMap(responseBody));
            }
        } catch (Exception e) {
            log.error("Error getting latest song from response: {}", e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        try {
            return objectMapper.convertValue(obj, Map.class);
        } catch (Exception e) {
            log.error("Error converting object to map: {}", e.getMessage(), e);
            return Map.of();
        }
    }
}

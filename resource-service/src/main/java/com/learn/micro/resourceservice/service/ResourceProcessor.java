package com.learn.micro.resourceservice.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.model.MetadataTag;
import com.learn.micro.resourceservice.model.ResourceMetadataDto;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ResourceProcessor {

    public boolean isValidMp3(byte[] fileContent) {
        if (fileContent == null || fileContent.length == 0) {
            return false;
        }
        Tika tika = new Tika();
        String fileType = tika.detect(fileContent);
        return "audio/mpeg".equalsIgnoreCase(fileType) || "audio/mp3".equalsIgnoreCase(fileType);
    }

    public ResourceMetadataDto getResourceMetadata(byte[] fileContent) {
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler();
        Mp3Parser parser = new Mp3Parser();

        try (InputStream inputStream = new ByteArrayInputStream(fileContent)) {
            parser.parse(inputStream, handler, metadata, null);
            return collectResourceMetadata(metadata);
        } catch (IOException | TikaException | SAXException e) {
            throw new GeneralFailureException(e.getMessage());
        }
    }

    private ResourceMetadataDto collectResourceMetadata(Metadata metadata) {
        return new ResourceMetadataDto(
                metadata.get(MetadataTag.NAME.getKey()),
                metadata.get(MetadataTag.ARTIST.getKey()),
                metadata.get(MetadataTag.ALBUM.getKey()),
                extractDuration(metadata),
                metadata.get(MetadataTag.YEAR.getKey())
        );
    }

    private String extractDuration(Metadata metadata) {
        return Optional.ofNullable(metadata.get("xmpDM:duration"))
                .map(duration -> formatDuration(Double.parseDouble(duration)))
                .orElse(null);
    }

    private String formatDuration(double durationInSeconds) {
        int minutes = (int) (durationInSeconds / 60);
        int seconds = (int) (durationInSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}

package com.learn.micro.resourceservice.model;

import lombok.Data;

@Data
public class ResourceMetadataDto {

    Integer id;
    String name;
    String artist;
    String album;
    String duration;
    String year;

    public ResourceMetadataDto(String name, String artist, String album, String duration,
            String year) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
    }
}

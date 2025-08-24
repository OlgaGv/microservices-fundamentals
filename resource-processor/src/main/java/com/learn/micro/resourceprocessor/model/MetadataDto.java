package com.learn.micro.resourceprocessor.model;

import lombok.Data;

@Data
public class MetadataDto {

    Integer id;
    String name;
    String artist;
    String album;
    String duration;
    String year;

    public MetadataDto(String name, String artist, String album, String duration,
            String year) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
    }
}

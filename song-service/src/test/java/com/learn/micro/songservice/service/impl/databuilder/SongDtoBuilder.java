package com.learn.micro.songservice.service.impl.databuilder;

import com.learn.micro.songservice.model.SongDto;

public class SongDtoBuilder {

    private Integer id = 1;
    private String name = "Test Song";
    private String artist = "Test Artist";
    private String album = "Test Album";
    private String duration = "03:45"; // valid mm:ss format
    private String year = "2023";

    private SongDtoBuilder() {
    }

    public static SongDtoBuilder songDto() {
        return new SongDtoBuilder();
    }

    public SongDtoBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public SongDtoBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SongDtoBuilder withArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public SongDtoBuilder withAlbum(String album) {
        this.album = album;
        return this;
    }

    public SongDtoBuilder withDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public SongDtoBuilder withYear(String year) {
        this.year = year;
        return this;
    }

    public SongDto build() {
        return new SongDto(id, name, artist, album, duration, year);
    }
}
package com.learn.micro.songservice.mapper;


import com.learn.micro.songservice.entity.SongEntity;
import com.learn.micro.songservice.model.SaveSongResponse;
import com.learn.micro.songservice.model.SongDto;

public interface SongMapper {

    SongDto mapEntityToSongDto(SongEntity from);

    SongEntity mapSongDtoToEntity(SongDto from);

    SaveSongResponse mapEntityToSavedSongDto(SongEntity from);
}

package com.learn.micro.songservice.mapper;

import com.learn.micro.songservice.entity.SongEntity;
import com.learn.micro.songservice.model.SaveSongResponse;
import com.learn.micro.songservice.model.SongDto;
import org.springframework.stereotype.Component;

@Component
public class SongMapperImpl implements SongMapper {

    @Override
    public SongDto mapEntityToSongDto(SongEntity from) {
        return new SongDto(from.getId(),
                from.getName(),
                from.getArtist(),
                from.getAlbum(),
                from.getDuration(),
                from.getYear());
    }

    @Override
    public SongEntity mapSongDtoToEntity(SongDto from) {
        SongEntity songEntity = new SongEntity();
        songEntity.setId(from.getId());
        songEntity.setName(from.getName());
        songEntity.setArtist(from.getArtist());
        songEntity.setAlbum(from.getAlbum());
        songEntity.setDuration(from.getDuration());
        songEntity.setYear(from.getYear());
        return songEntity;
    }

    @Override
    public SaveSongResponse mapEntityToSavedSongDto(SongEntity from) {
        return new SaveSongResponse(from.getId());
    }
}

package com.learn.micro.songservice.service;

import com.learn.micro.songservice.model.DeleteSongResponse;
import com.learn.micro.songservice.model.SaveSongResponse;
import com.learn.micro.songservice.model.SongDto;
import java.util.List;

public interface SongService {

    SaveSongResponse save(SongDto songDto);

    SongDto getById(String id);

    DeleteSongResponse delete(String id);

    List<SongDto> getAll();
}

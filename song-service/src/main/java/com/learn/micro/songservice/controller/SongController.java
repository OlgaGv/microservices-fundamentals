package com.learn.micro.songservice.controller;

import com.learn.micro.songservice.model.DeleteSongResponse;
import com.learn.micro.songservice.model.SaveSongResponse;
import com.learn.micro.songservice.model.SongDto;
import com.learn.micro.songservice.service.SongService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<SaveSongResponse> save(@Validated @RequestBody SongDto songDto) {
        SaveSongResponse result = songService.save(songDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> findById(@PathVariable String id) {
        SongDto result = songService.getById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<SongDto>> findAll() {
        List<SongDto> result = songService.getAll();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> delete(@RequestParam("id") String id) {
        DeleteSongResponse result = songService.delete(id);
        return ResponseEntity.ok(result);
    }
}

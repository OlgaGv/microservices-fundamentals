package com.learn.micro.songservice.service.impl;

import static java.util.stream.Collectors.toList;

import com.learn.micro.songservice.entity.SongEntity;
import com.learn.micro.songservice.exception.GeneralFailureException;
import com.learn.micro.songservice.mapper.SongMapper;
import com.learn.micro.songservice.model.DeleteSongResponse;
import com.learn.micro.songservice.model.SaveSongResponse;
import com.learn.micro.songservice.model.SongDto;
import com.learn.micro.songservice.repository.SongRepository;
import com.learn.micro.songservice.service.MessageHelper;
import com.learn.micro.songservice.service.SongService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final MessageHelper messageHelper;

    public SaveSongResponse save(SongDto songDto) {
        if (songDto != null && songDto.getId() != null) {
            Integer songId = songDto.getId();
            songRepository.findById(songId)
                    .ifPresent(entity -> {
                        throw new EntityExistsException(MessageFormat.format(
                                messageHelper.getMessage("error.metadata.already.exists"),
                                entity.getId()));
                    });
        }
        SongEntity songToSave = songMapper.mapSongDtoToEntity(songDto);
        return Optional.of(songRepository.save(songToSave))
                .map(songMapper::mapEntityToSavedSongDto)
                .orElseThrow(() -> new GeneralFailureException("server.error.general"));
    }

    public SongDto getById(String id) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException(
                    MessageFormat.format(messageHelper.getMessage("validation.id.invalid"), id));
        }
        Integer songId = Integer.parseInt(id);
        return songRepository.findById(songId)
                .map(songMapper::mapEntityToSongDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageFormat.format(messageHelper.getMessage("error.metadata.not.found"),
                                id)));
    }

    public DeleteSongResponse delete(String ids) {
        List<Integer> idsToDelete = parseAndValidateIds(ids);
        List<Integer> deletedIds = new ArrayList<>();
        for (Integer id : idsToDelete) {
            if (songRepository.existsById(id)) {
                songRepository.deleteById(id);
                deletedIds.add(id);
            }
        }
        return new DeleteSongResponse(deletedIds);
    }

    public List<SongDto> getAll() {
        return songRepository.findAll()
                .stream()
                .map(songMapper::mapEntityToSongDto)
                .collect(toList());
    }

    private boolean isValidId(String id) {
        return id.matches("^[1-9]\\d*$");
    }

    private void validateIds(String ids) {
        if (ids.length() >= 200) {
            throw new IllegalArgumentException(
                MessageFormat.format(messageHelper.getMessage("validation.ids.length"), ids.length()));
        }
        if (!ids.matches("^\\d+(,\\d+)*$")) {
            throw new IllegalArgumentException(
                    messageHelper.getMessage("validation.ids.invalid"));
        }
    }

    private List<Integer> parseAndValidateIds(String idParam) {
        validateIds(idParam);
        return Arrays.stream(idParam.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .map(Integer::parseInt)
                .filter(id -> id > 0)
                .toList();
    }
}

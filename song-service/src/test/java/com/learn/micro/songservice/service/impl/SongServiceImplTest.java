package com.learn.micro.songservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.learn.micro.songservice.entity.SongEntity;
import com.learn.micro.songservice.mapper.SongMapper;
import com.learn.micro.songservice.model.DeleteSongResponse;
import com.learn.micro.songservice.model.SaveSongResponse;
import com.learn.micro.songservice.model.SongDto;
import com.learn.micro.songservice.repository.SongRepository;
import com.learn.micro.songservice.service.MessageHelper;
import com.learn.micro.songservice.service.impl.databuilder.SongDtoBuilder;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

    private static final String TEST_SONG_NAME="Test Song";
    private static final String TEST_SONG_ARTIST="Test Artist";
    private static final String TEST_SONG_ALBUM="Test Album";
    private static final String TEST_SONG_DURATION="03:45";
    private static final String TEST_SONG_YEAR="2023";
    private static final String SONG_IDS_TO_DELETE = "1,2";

    @Mock
    private SongRepository songRepository;
    @Mock
    private SongMapper songMapper;
    @Mock
    private MessageHelper messageHelper;

    @InjectMocks
    private SongServiceImpl songService;

    private SongDto songDto;
    private SongEntity songEntity;

    @BeforeEach
    void setUp() {
        songDto = SongDtoBuilder.songDto().build();
        songEntity = new SongEntity();
        songEntity.setId(1);
        songEntity.setName(TEST_SONG_NAME);
        songEntity.setArtist(TEST_SONG_ARTIST);
        songEntity.setAlbum(TEST_SONG_ALBUM);
        songEntity.setDuration(TEST_SONG_DURATION);
        songEntity.setYear(TEST_SONG_YEAR);
    }

    /**
     * Test that saving a song with an existing ID
     * should throw {@link EntityExistsException}.
     */
    @Test
    void save_shouldThrowEntityExistsException_whenSongAlreadyExists() {
        // given
        when(songRepository.findById(1)).thenReturn(Optional.of(songEntity));
        when(messageHelper.getMessage("error.metadata.already.exists"))
            .thenReturn("Song with id {0} already exists");
        // when and then
        assertThrows(EntityExistsException.class, () -> songService.save(songDto));
    }

    /**
     * Test that saving a song when it does not exist
     * should persist the entity and return a {@link SaveSongResponse}.
     */
    @Test
    void save_shouldSaveSong_whenSongDoesNotExist() {
        // given
        when(songRepository.findById(1)).thenReturn(Optional.empty());
        when(songMapper.mapSongDtoToEntity(songDto)).thenReturn(songEntity);
        when(songRepository.save(songEntity)).thenReturn(songEntity);
        SaveSongResponse expectedResponse = new SaveSongResponse(1);
        when(songMapper.mapEntityToSavedSongDto(songEntity)).thenReturn(expectedResponse);
        // when
        SaveSongResponse response = songService.save(songDto);
        // then
        assertNotNull(response);
        assertEquals(1, response.id());
        verify(songRepository).save(songEntity);
    }

    /**
     * Test that retrieving a song with an invalid ID
     * should throw {@link IllegalArgumentException}.
     */
    @Test
    void getById_shouldThrowIllegalArgumentException_whenInvalidId() {
        // given
        when(messageHelper.getMessage("validation.id.invalid"))
            .thenReturn("Invalid id: {0}");
        // when and then
        assertThrows(IllegalArgumentException.class, () -> songService.getById("abc"));
    }

    /**
     * Test that retrieving a song by ID when it does not exist
     * should throw {@link ResourceNotFoundException}.
     */
    @Test
    void getById_shouldThrowResourceNotFound_whenSongNotFound() {
        // given
        when(songRepository.findById(1)).thenReturn(Optional.empty());
        when(messageHelper.getMessage("error.metadata.not.found"))
            .thenReturn("Song not found with id {0}");
        // when and then
        assertThrows(ResourceNotFoundException.class, () -> songService.getById("1"));
    }

    /**
     * Test that retrieving a song by ID when it exists
     * should return a mapped {@link SongDto}.
     */
    @Test
    void getById_shouldReturnSongDto_whenSongFound() {
        // given
        when(songRepository.findById(1)).thenReturn(Optional.of(songEntity));
        when(songMapper.mapEntityToSongDto(songEntity)).thenReturn(songDto);
        // when
        SongDto result = songService.getById("1");
        // then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(TEST_SONG_NAME, result.getName());
    }

    /**
     * Test that deleting songs with valid IDs
     * should remove them from the repository and return deleted IDs.
     */
    @Test
    void delete_shouldDeleteExistingSongs() {
        // given
        when(songRepository.existsById(anyInt())).thenReturn(true);
        // when
        DeleteSongResponse response = songService.delete(SONG_IDS_TO_DELETE);
        // then
        assertEquals(List.of(1, 2), response.ids());
        verify(songRepository, times(2)).deleteById(anyInt());
    }

    /**
     * Test that deleting songs should skip non-existing IDs
     * and only delete those that exist.
     */
    @Test
    void delete_shouldSkipNonExistingSongs() {
        // given
        when(songRepository.existsById(1)).thenReturn(true);
        when(songRepository.existsById(2)).thenReturn(false);
        // when
        DeleteSongResponse response = songService.delete(SONG_IDS_TO_DELETE);
        // then
        assertEquals(List.of(1), response.ids());
        verify(songRepository).deleteById(1);
        verify(songRepository, never()).deleteById(2);
    }

    /**
     * Test that retrieving all songs
     * should return a list of mapped {@link SongDto}.
     */
    @Test
    void getAll_shouldReturnMappedDtos() {
        // given
        when(songRepository.findAll()).thenReturn(List.of(songEntity));
        when(songMapper.mapEntityToSongDto(songEntity)).thenReturn(songDto);
        // when
        List<SongDto> songs = songService.getAll();
        // then
        assertEquals(1, songs.size());
        assertEquals(TEST_SONG_NAME, songs.get(0).getName());
    }
}
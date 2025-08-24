package com.learn.micro.songservice.model;

import com.learn.micro.songservice.validator.YearRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SongDto {

    Integer id;
    @NotNull(message = "Name is required.")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters.")
    String name;
    @NotBlank(message = "Artist is required.")
    @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters.")
    String artist;
    @NotBlank(message = "Album is required.")
    @Size(min = 1, max = 100, message = "Album must be between 1 and 100 characters.")
    String album;
    @NotBlank(message = "Duration is required.")
    @Pattern(regexp = "^([0-5]\\d):([0-5]\\d)$", message = "Invalid mm:ss duration")
    String duration;
    @NotNull(message = "Year is required.")
    @YearRange
    String year;
}

package com.learn.micro.songservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="song")
public class SongEntity {

    @Id
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String artist;
    @NotNull
    private String album;
    @NotNull
    private String duration;
    @NotNull
    private String year;
}

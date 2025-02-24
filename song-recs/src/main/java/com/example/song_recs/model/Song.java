package com.example.song_recs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "songs")
public class Song {
    // change
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "trackId")
    private String trackId;
    @Column(name = "artist_id")
    private String artist_id;
    @Column(name = "genre")
    private String genre;
    @Column(name = "album_id")
    private String album_id;
    @Column(name = "tempo")
    private double tempo;
    @Column(name = "name")
    private String track;
    @Column(name = "artist")
    private String artist;
    @Column(name = "album")
    private String album;


    public Song() {}

    public Song(String trackId, String artist_id, String genre, String album_id, double tempo, String track, String artist, String album) {
        this.tempo = tempo;
        this.trackId = trackId;
        this.artist_id = artist_id;
        this.genre = genre;
        this.album_id = album_id;
        this.track = track;
        this.artist = artist;
        this.album = album;
    }
}

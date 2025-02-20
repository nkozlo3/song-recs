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

    @Column(name = "track_id")
    private String track_id;
    @Column(name = "artist_id")
    private String artist_id;
    @Column(name = "genre_id")
    private String genre_id;
    @Column(name = "album_id")
    private String album_id;
    @Column(name = "tempo")
    private String tempo;
    @Column(name = "title")
    private String title;

    public Song() {}

    public Song(String track_id, String artist_id, String genre_id, String album_id, String tempo) {
        this.track_id = track_id;
        this.artist_id = artist_id;
        this.genre_id = genre_id;
        this.album_id = album_id;
        this.tempo = tempo;
    }
}

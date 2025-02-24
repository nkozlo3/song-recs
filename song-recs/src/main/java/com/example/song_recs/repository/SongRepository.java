package com.example.song_recs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.song_recs.model.Song;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByTrackContainingIgnoreCaseOrArtistContainingIgnoreCaseOrAlbumContainingIgnoreCase(String track, String artist, String album);

    List<Song> findByTempoGreaterThanEqualAndTempoLessThanEqualAndGenreContainingIgnoreCase(double minTempo, double maxTempo, String genre);

    Song findByTrackId(String trackId);

    boolean existsByTrackId(String trackId);

}

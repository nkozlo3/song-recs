package com.example.song_recs.controller;

import com.example.song_recs.repository.SongRepository;
import com.example.song_recs.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.song_recs.model.Song;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {

    @Autowired
    SongRepository songRepository;
    @Autowired
    SongService songService;

    @GetMapping("/song/{title}")
    public List<Song> getSongInformation(@PathVariable String title) {
        return songRepository.findByTitleContainingIgnoreCase(title);
    }

}

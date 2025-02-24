package com.example.song_recs.controller;

import com.example.song_recs.repository.SongRepository;
import com.example.song_recs.service.SongService;
import com.example.song_recs.model.Song;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import java.io.IOException;
import java.util.List;

@Controller
public class WebController {
    private final SongService songService;

    public WebController(SongRepository songRepository) {
        this.songService = new SongService(songRepository);
    }

    @GetMapping("/search")
    public String search() {
        return "search"; // thymeleaf ✨magic✨
    }

    @GetMapping("/results")
    public String results(@RequestParam("songTitle") String songTitle, Model model) throws IOException {
        List<Song> suggestions = songService.getCachedSongsOrGetAndPopulate(songTitle);
        model.addAttribute("suggestions", suggestions);

        return "results"; // thymeleaf ✨magic✨ again !
    }

}

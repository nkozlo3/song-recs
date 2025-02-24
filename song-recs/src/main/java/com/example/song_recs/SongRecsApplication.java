package com.example.song_recs;

import com.example.song_recs.service.SongService;
import okhttp3.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Base64;

@SpringBootApplication
@Component
public class SongRecsApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SongRecsApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(SongService songService) throws IOException {
        return args -> {
//            songService.getCachedSongsOrGetAndPopulate("love");
            songService.getCachedSongsByTempoAndGenreOrGetAndPopulate(100, "Rock");
        };
    }
}

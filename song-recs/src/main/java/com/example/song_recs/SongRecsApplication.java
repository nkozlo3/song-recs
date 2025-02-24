package com.example.song_recs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;


import java.io.IOException;

@SpringBootApplication
@Component
public class SongRecsApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SongRecsApplication.class, args);
    }
}

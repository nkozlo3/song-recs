package com.example.song_recs;

import com.example.song_recs.service.SongService;
import okhttp3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;


import java.io.IOException;
import java.util.Base64;

@SpringBootApplication
public class SongRecsApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SongRecsApplication.class, args);
    }


}

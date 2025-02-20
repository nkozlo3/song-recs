package com.example.song_recs.service;


import com.example.song_recs.repository.SongRepository;
import com.example.song_recs.model.Song;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class SongService {
    private final SongRepository songRepository;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;


    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
        this.okHttpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Song> getCachedSongsOrGetAndPopulate(String query) throws IOException {
        List<Song> songs = songRepository.findByTitleContainingIgnoreCase(query);
        if (songs.isEmpty()) {
            populateDatabase(query);
            songs = songRepository.findByTitleContainingIgnoreCase(query);
        }

        return songs;
    }

    public String getAccessToken() throws IOException {
        String token_URL = "https://accounts.spotify.com/api/token";
        String credentials = System.getenv("CLIENT_ID") + ":" + System.getenv("CLIENT_SECRET");
        credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        OkHttpClient client = new OkHttpClient();


        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(token_URL)
                .addHeader("Authorization", "Basic " + credentials)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            JSONObject object = new JSONObject(response.body().string());
            return object.getString("access_token");
        }
    }

    private void populateDatabase(String query) throws IOException {

    }


}

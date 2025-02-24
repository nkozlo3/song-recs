package com.example.song_recs.service;


import com.example.song_recs.repository.SongRepository;
import com.example.song_recs.model.Song;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public List<Song> getCachedSongsByTempoAndGenreOrGetAndPopulate(String genre, double tempo) {


        return new ArrayList<>();
    }

    public List<Song> getCachedSongsOrGetAndPopulate(String query) throws IOException {
        List<Song> songs = songRepository.findByTrackContainingIgnoreCase(query);
        if (songs.isEmpty() || songs.size() < 15) {
            System.out.println("POPULATED THIS TIME");
            populateDatabase(query);
            songs = songRepository.findByTrackContainingIgnoreCase(query);
        } else {
            System.out.println("Did not populate that time");
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
        String url = "https://api.deezer.com/search?q=track:\"" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "\"&order=RANKING&limit=50";

        Request request = new Request.Builder().url(url).get().build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JsonNode root = objectMapper.readTree(response.body().string());
            JsonNode items = root.path("data");

            for (JsonNode item : items) {
                String track = item.get("title").asText();
                String trackId = item.get("id").asText();


                JsonNode album = item.path("album");
                String album_name = album.get("title").asText();
                String album_id = album.get("id").asText();


                JsonNode artist = item.path("artist");
                String artist_name = artist.get("name").asText();
                String artist_id = artist.get("id").asText();
                String genre_name = "";
                double tempo = 0;


                String tempoUrl = "https://api.deezer.com/track/" + URLEncoder.encode(trackId, StandardCharsets.UTF_8);
                Request tempoRequest = new Request.Builder().url(tempoUrl).get().build();

                try (Response tempoResponse = okHttpClient.newCall(tempoRequest).execute()) {
                    if (!tempoResponse.isSuccessful()) {
                        throw new IOException("Unexpected code " + tempoResponse);
                    }
                    JsonNode tempoRoot = objectMapper.readTree(tempoResponse.body().string());
                    tempo = tempoRoot.get("bpm").asDouble();
                }

                String genreURL = "https://api.deezer.com/album/" + URLEncoder.encode(album_id, StandardCharsets.UTF_8);
                Request genreRequest = new Request.Builder().url(genreURL).get().build();

                try (Response genreResponse = okHttpClient.newCall(genreRequest).execute()) {
                    if (!genreResponse.isSuccessful()) {
                        throw new IOException("Unexpected code " + genreResponse);
                    }
                    JsonNode genreRoot = objectMapper.readTree(genreResponse.body().string());
                    JsonNode genre = genreRoot.get("genres").get("data");
                    if (genre.get(0) != null) {
                        genre_name = genre.get(0).get("name").asText();
                    }
                }

                if (!genre_name.isEmpty() && tempo != 0 && songRepository.findByTrackId(trackId) == null) {
                    Song song = new Song(trackId, artist_id, genre_name, album_id, tempo, track, artist_name, album_name);
                    songRepository.save(song);
                }
            }
        }
    }
}

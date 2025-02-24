package com.example.song_recs.service;


import com.example.song_recs.repository.SongRepository;
import com.example.song_recs.model.Song;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static java.rmi.server.LogStream.log;

@Service
public class SongService {
    private final SongRepository songRepository;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final String genre_ = "BADGENRE";
    private static final Logger logger = LoggerFactory.getLogger(SongService.class);


    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
        this.okHttpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Song> getCachedSongsByTempoAndGenreOrGetAndPopulate(double tempo, String genre) throws IOException {
        List<Song> songs = songRepository.findByTempoGreaterThanEqualAndTempoLessThanEqualAndGenreContainingIgnoreCase(tempo - 20, tempo + 20, genre);
        if (songs.isEmpty() || songs.size() < 20) {
            logger.info("Finding new songs");
            populateDatabase("irrelevant", tempo);
            songs = songRepository.findByTempoGreaterThanEqualAndTempoLessThanEqualAndGenreContainingIgnoreCase(tempo - 20, tempo + 20, genre);
        } else {
            logger.info("Songs already exist, hurray for cache");
        }
        return songs;
    }

    public List<Song> getCachedSongsOrGetAndPopulate(String query) throws IOException {
        List<Song> songs = songRepository.findByTrackContainingIgnoreCaseOrArtistContainingIgnoreCaseOrAlbumContainingIgnoreCase(query, query, query);
        if (songs.isEmpty() || songs.size() < 10) {
            logger.info("finding new songs, this may take a while");
            populateDatabase(query, -100);
            songs = songRepository.findByTrackContainingIgnoreCaseOrArtistContainingIgnoreCaseOrAlbumContainingIgnoreCase(query, query, query);
        } else {
            logger.info("songs already exist, hurray for cache");
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

    private void saveSongsFromQuery(String url) throws IOException {
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

                if (!genre_name.isEmpty() && !songRepository.existsByTrackId(trackId) && tempo != 0) {
                    Song song = new Song(trackId, artist_id, genre_name, album_id, tempo, track, artist_name, album_name);
                    logger.info("NEW SONG ADDED GUYS IT HAS name {}", track);

                    songRepository.save(song);
                }
            }
        }
    }

    private void populateDatabase(String query, double tempo_) throws IOException {
        String url = "";
        if (tempo_ == -100) {
            List<String> queryPossibilities = Arrays.asList("track:\"", "artist:\"", "album:\"");
            for (String possibility : queryPossibilities) {
                url = "https://api.deezer.com/search?q=" + possibility + URLEncoder.encode(query, StandardCharsets.UTF_8) + "\"&order=RANKING&limit=50";
                saveSongsFromQuery(url);
            }
        } else {
            url = "https://api.deezer.com/search?q=bpm_min=" + Double.toString(tempo_ - 20) + "&bpm_max=" + Double.toString(tempo_ + 20) + "&limit=50";
            saveSongsFromQuery(url);
        }

    }
}

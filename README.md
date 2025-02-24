# Song Recommendations

This is a Spring Boot application that provides song recommendations based on track, artist, album, genre, and tempo. It uses Deezer API to fetch song data and caches the results in a MySQL database.

## Table of Contents

- [Technologies](#technologies)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Endpoints](#endpoints)


## Technologies

- Java 23
- Spring Boot
- Thymeleaf
- MySQL
- Deezer API
- OkHttp
- Lombok


## Features

- Search for songs by title, artist, or album.
- Get song recommendations based on genre and tempo.
- Caches song data to improve performance.


## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/your-username/song-recs.git
    cd song-recs
    ```

2. Set up the MySQL database:
    ```sql
    CREATE DATABASE song_recs_db;
    ```

3. Update the database credentials in `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/song_recs_db?allowPublicKeyRetrieval=true&useSSL=false
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    ```

4. Build and run the application:
    ```sh
    ./mvnw spring-boot:run
    ```


## Usage

- Open your browser and go to `http://localhost:8080/search` to search for songs.
- Use the `/results` endpoint to get song recommendations based on the search query.
- Use the `/suggestions` endpoint to get song recommendations based on genre and tempo.


## Endpoints

- `GET /search`: Search for songs.
- `GET /results`: Get song recommendations based on the search query.
- `GET /suggestions`: Get song recommendations based on genre and tempo.




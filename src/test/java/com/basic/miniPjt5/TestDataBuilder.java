package com.basic.miniPjt5;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.response.*;

import java.util.List;

// 테스트 데이터 빌더 클래스
public class TestDataBuilder {

    public static Genre createMockGenre() {
        return Genre.builder()
                .id(28L)
                .name("Action")
                .build();
    }

    public static List<Genre> createMockGenres() {
        return List.of(
                Genre.builder().id(28L).name("Action").build(),
                Genre.builder().id(35L).name("Comedy").build(),
                Genre.builder().id(18L).name("Drama").build()
        );
    }

    public static Movie createMockMovie() {
        return Movie.builder()
                .id(1L)
                .tmdbId(550L)
                .title("Test Movie")
                .overview("Test movie overview")
                .releaseDate("2023-01-01")
                .posterPath("/test-poster.jpg")
                .voteAverage(8.5)
                .voteCount(1000)
                .genres(List.of(createMockGenre()))
                .build();
    }

    public static List<Movie> createMockMovies() {
        return List.of(
                Movie.builder()
                        .id(1L).tmdbId(1L).title("The Avengers")
                        .voteAverage(8.0).voteCount(5000)
                        .genres(List.of(createMockGenre())).build(),
                Movie.builder()
                        .id(2L).tmdbId(2L).title("Avengers: Endgame")
                        .voteAverage(8.4).voteCount(8000)
                        .genres(List.of(createMockGenre())).build()
        );
    }

    public static Drama createMockDrama() {
        return Drama.builder()
                .id(1L)
                .tmdbId(1399L)
                .title("Test Drama")
                .overview("Test drama overview")
                .firstAirDate("2023-01-01")
                .posterPath("/test-drama-poster.jpg")
                .numberOfSeasons(3)
                .numberOfEpisodes(30)
                .voteAverage(9.0)
                .voteCount(2000)
                .genres(List.of(createMockGenre()))
                .build();
    }

    public static List<Drama> createMockDramas() {
        return List.of(
                Drama.builder()
                        .id(1L).tmdbId(100L).title("Test Drama 1")
                        .numberOfSeasons(5).voteAverage(8.8).build(),
                Drama.builder()
                        .id(2L).tmdbId(101L).title("Test Drama 2")
                        .numberOfSeasons(3).voteAverage(8.2).build()
        );
    }

    public static TMDBMovie createMockTMDBMovie() {
        TMDBMovie movie = new TMDBMovie();
        movie.setId(550L);
        movie.setTitle("Mock TMDB Movie");
        movie.setOverview("Mock overview");
        movie.setReleaseDate("2023-01-01");
        movie.setPosterPath("/mock-poster.jpg");
        movie.setBackdropPath("/mock-backdrop.jpg");
        movie.setVoteAverage(8.5);
        movie.setVoteCount(1000);
        movie.setGenreIds(List.of(28, 12)); // Action, Adventure
        movie.setPopularity(100.0);
        movie.setOriginalLanguage("en");
        movie.setAdult(false);
        return movie;
    }

    public static TMDBMovieResponse createMockTMDBMovieResponse() {
        TMDBMovieResponse response = new TMDBMovieResponse();
        response.setResults(List.of(createMockTMDBMovie()));
        response.setTotalPages(1);
        response.setTotalResults(1);
        return response;
    }

    public static TMDBDrama createMockTMDBTV() {
        TMDBDrama drama = new TMDBDrama();
        drama.setId(1399L);
        drama.setName("Mock TMDB TV");
        drama.setOverview("Mock TV overview");
        drama.setFirstAirDate("2023-01-01");
        drama.setPosterPath("/mock-tv-poster.jpg");
        drama.setBackdropPath("/mock-tv-backdrop.jpg");
        drama.setVoteAverage(9.0);
        drama.setVoteCount(2000);
        drama.setGenreIds(List.of(18, 10765)); // Drama, Sci-Fi & Fantasy
        drama.setPopularity(150.0);
        drama.setOriginalLanguage("en");
        drama.setNumberOfSeasons(8);
        drama.setNumberOfEpisodes(73);
        return drama;
    }

    public static TMDBTVResponse createMockTMDBTVResponse() {
        TMDBTVResponse response = new TMDBTVResponse();
        response.setResults(List.of(createMockTMDBTV()));
        response.setTotalPages(1);
        response.setTotalResults(1);
        return response;
    }

    public static TMDBGenre createMockTMDBGenre() {
        TMDBGenre genre = new TMDBGenre();
        genre.setId(28);
        genre.setName("Action");
        return genre;
    }

    public static TMDBGenreResponse createMockTMDBGenreResponse() {
        TMDBGenreResponse response = new TMDBGenreResponse();
        response.setGenres(List.of(
                createMockTMDBGenre(),
                createMockTMDBGenre2()
        ));
        return response;
    }

    private static TMDBGenre createMockTMDBGenre2() {
        TMDBGenre genre = new TMDBGenre();
        genre.setId(35);
        genre.setName("Comedy");
        return genre;
    }
}
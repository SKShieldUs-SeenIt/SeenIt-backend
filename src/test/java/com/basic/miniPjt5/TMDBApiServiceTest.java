package com.basic.miniPjt5;

import com.basic.miniPjt5.response.*;
import com.basic.miniPjt5.service.TMDBApiService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

// TMDB API 서비스 테스트
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TMDBApiServiceTest {

    @Autowired
    private TMDBApiService tmdbApiService;

    @Test
    @Order(1)
    @DisplayName("영화 장르 목록 조회 테스트")
    void testGetMovieGenres() {
        // given & when
        TMDBGenreResponse response = tmdbApiService.getMovieGenres();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getGenres()).isNotEmpty();
        assertThat(response.getGenres().get(0).getId()).isNotNull();
        assertThat(response.getGenres().get(0).getName()).isNotBlank();
        
        System.out.println("영화 장르 수: " + response.getGenres().size());
        response.getGenres().forEach(genre -> 
            System.out.println("장르: " + genre.getName() + " (ID: " + genre.getId() + ")")
        );
    }

    @Test
    @Order(2)
    @DisplayName("TV 장르 목록 조회 테스트")
    void testGetTVGenres() {
        // given & when
        TMDBGenreResponse response = tmdbApiService.getTVGenres();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getGenres()).isNotEmpty();
        
        System.out.println("TV 장르 수: " + response.getGenres().size());
    }

    @Test
    @Order(3)
    @DisplayName("인기 영화 목록 조회 테스트")
    void testGetPopularMovies() {
        // given
        int page = 1;

        // when
        TMDBMovieResponse response = tmdbApiService.getPopularMovies(page);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getResults()).isNotEmpty();
        assertThat(response.getResults().size()).isLessThanOrEqualTo(20);
        
        TMDBMovie firstMovie = response.getResults().get(0);
        assertThat(firstMovie.getId()).isNotNull();
        assertThat(firstMovie.getTitle()).isNotBlank();
        
        System.out.println("첫 번째 인기 영화: " + firstMovie.getTitle());
        System.out.println("평점: " + firstMovie.getVoteAverage());
        System.out.println("장르 ID들: " + firstMovie.getGenreIds());
    }

    @Test
    @Order(4)
    @DisplayName("인기 TV 프로그램 목록 조회 테스트")
    void testGetPopularTVShows() {
        // given
        int page = 1;

        // when
        TMDBTVResponse response = tmdbApiService.getPopularTVShows(page);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getResults()).isNotEmpty();
        
        TMDBDrama firstTV = response.getResults().get(0);
        assertThat(firstTV.getId()).isNotNull();
        assertThat(firstTV.getName()).isNotBlank();
        
        System.out.println("첫 번째 인기 TV: " + firstTV.getName());
        System.out.println("시즌 수: " + firstTV.getNumberOfSeasons());
    }

    @Test
    @Order(5)
    @DisplayName("영화 검색 테스트")
    void testSearchMovies() {
        // given
        String query = "avengers";
        int page = 1;

        // when
        TMDBMovieResponse response = tmdbApiService.searchMovies(query, page);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getResults()).isNotEmpty();
        
        // 검색 결과에 "avengers"가 포함되어 있는지 확인
        boolean hasAvengers = response.getResults().stream()
                .anyMatch(movie -> movie.getTitle().toLowerCase().contains("avengers"));
        assertThat(hasAvengers).isTrue();
        
        System.out.println("'" + query + "' 검색 결과 수: " + response.getResults().size());
    }

    @Test
    @Order(6)
    @DisplayName("TV 프로그램 검색 테스트")
    void testSearchTVShows() {
        // given
        String query = "friends";
        int page = 1;

        // when
        TMDBTVResponse response = tmdbApiService.searchTVShows(query, page);

        // then
        assertThat(response).isNotNull();
        
        if (!response.getResults().isEmpty()) {
            System.out.println("'" + query + "' TV 검색 결과 수: " + response.getResults().size());
            System.out.println("첫 번째 결과: " + response.getResults().get(0).getName());
        }
    }
}
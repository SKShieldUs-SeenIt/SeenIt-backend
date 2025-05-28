package com.basic.miniPjt5;

import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// 통합 테스트
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        
        // 테스트 데이터 설정
        setupTestData();
    }

    private void setupTestData() {
        // 장르 데이터
        Genre actionGenre = Genre.builder().id(28L).name("Action").build();
        Genre dramaGenre = Genre.builder().id(18L).name("Drama").build();
        genreRepository.saveAll(List.of(actionGenre, dramaGenre));

        // 영화 데이터
        Movie movie = Movie.builder()
                .tmdbId(550L)
                .title("Fight Club")
                .overview("Test overview")
                .voteAverage(8.4)
                .voteCount(1000)
                .genres(List.of(actionGenre))
                .build();
        movieRepository.save(movie);
    }

    @Test
    @Order(1)
    @DisplayName("헬스 체크 API 테스트")
    void testHealthCheck() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/health", Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        
        System.out.println("헬스 체크 응답: " + response.getBody());
    }

    @Test
    @Order(2)
    @DisplayName("장르 목록 조회 API 테스트")
    void testGetGenres() {
        // when
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/content/genres", List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        
        System.out.println("장르 목록 크기: " + response.getBody().size());
    }

    @Test
    @Order(3)
    @DisplayName("영화 상세 조회 API 테스트")
    void testGetMovieById() {
        // given
        Long movieId = movieRepository.findByTmdbId(550L).get().getId();

        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/content/movies/" + movieId, Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("title")).isEqualTo("Fight Club");
        
        System.out.println("영화 상세 정보: " + response.getBody().get("title"));
    }

    @Test
    @Order(4)
    @DisplayName("영화 검색 API 테스트")
    void testSearchMovies() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/content/movies/search?query=fight&useApi=false", Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        System.out.println("검색 응답: " + response.getBody());
    }

    @Test
    @Order(5)
    @DisplayName("존재하지 않는 영화 조회 테스트")
    void testGetNonExistentMovie() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/content/movies/99999", Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        System.out.println("존재하지 않는 영화 조회 결과: " + response.getStatusCode());
    }
}
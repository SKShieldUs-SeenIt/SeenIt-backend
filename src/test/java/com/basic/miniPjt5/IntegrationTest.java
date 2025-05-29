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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// 통합 테스트
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스 레벨에서 인스턴스 관리
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

    @BeforeAll
    void setUpOnce() {
        baseUrl = "http://localhost:" + port + "/api";

        // 헬스 체크 대신 실제 API 준비 상태 확인
        waitForApiReady();

        System.out.println("=== 통합 테스트 시작 ===");
        System.out.println("총 영화 수: " + movieRepository.count());
        System.out.println("총 장르 수: " + genreRepository.count());
    }

    private void waitForApiReady() {
        int maxWaitTime = 30;
        int waitTime = 0;

        while (waitTime < maxWaitTime) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(
                        baseUrl + "/content/genres", String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("API 준비 완료!");
                    break;
                }
            } catch (Exception e) {
                // API가 아직 준비되지 않음
            }

            try {
                Thread.sleep(1000);
                waitTime++;
                System.out.println("API 준비 대기 중... (" + waitTime + "s)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 필요한 최소한의 설정만
        baseUrl = "http://localhost:" + port + "/api";
    }

    private void waitForDataInitialization() {
        // TMDB 데이터 초기화 완료까지 최대 30초 대기
        int maxWaitTime = 30;
        int waitTime = 0;

        while (movieRepository.count() < 10 && waitTime < maxWaitTime) {
            try {
                Thread.sleep(1000);
                waitTime++;
                System.out.println("데이터 초기화 대기 중... (" + waitTime + "s)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (movieRepository.count() < 10) {
            System.out.println("경고: 데이터 초기화가 완료되지 않았을 수 있습니다.");
        }
    }

    @Test
    @Order(1)
    @DisplayName("헬스 체크 API 테스트")
    void testHealthCheck() {
        // when - baseUrl에서 /api 제거하고 직접 /actuator/health 호출
        String healthUrl = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

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
    @DisplayName("영화 목록 조회 API 테스트")
    void testGetMovies() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/content/movies/popular?page=0&size=10", List.class);

        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);

        System.out.println("영화 목록 응답: " + response.getStatusCode());
    }

    @Test
    @Order(4)
    @DisplayName("실제 영화 상세 조회 API 테스트")
    void testGetMovieById() {
        // given - 실제 존재하는 영화 찾기
        List<Movie> movies = movieRepository.findAll();
        assertThat(movies).isNotEmpty();

        Movie firstMovie = movies.get(0);

        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/content/movies/" + firstMovie.getId(), Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("title")).isNotNull();

        System.out.println("영화 상세 정보: " + response.getBody().get("title"));
    }

    @Test
    @Order(5)
    @DisplayName("영화 검색 API 테스트")
    void testSearchMovies() {
        // given - 실제 존재하는 영화 제목으로 검색
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            System.out.println("검색할 영화가 없습니다. 테스트를 건너뜁니다.");
            return;
        }

        String searchTerm = movies.get(0).getTitle().substring(0, 3); // 첫 3글자로 검색

        // when
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/content/movies/search?query=" + searchTerm + "&useApi=false",
                List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        System.out.println("'" + searchTerm + "' 검색 결과: " + response.getStatusCode());
    }

    @Test
    @Order(6)
    @DisplayName("존재하지 않는 영화 조회 테스트")
    void testGetNonExistentMovie() {
        // when
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/content/movies/99999", Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        System.out.println("존재하지 않는 영화 조회 결과: " + response.getStatusCode());
    }

    @Test
    @Order(7)
    @DisplayName("데이터 통계 확인 테스트")
    void testDataStatistics() {
        long movieCount = movieRepository.count();
        long genreCount = genreRepository.count();

        assertThat(movieCount).isGreaterThan(0);
        assertThat(genreCount).isGreaterThan(0);

        System.out.println("=== 최종 데이터 통계 ===");
        System.out.println("총 영화 수: " + movieCount);
        System.out.println("총 장르 수: " + genreCount);

        // 샘플 영화 정보 출력
        List<Movie> sampleMovies = movieRepository.findAll().stream()
                .limit(5)
                .toList();

        System.out.println("샘플 영화들:");
        sampleMovies.forEach(movie ->
                System.out.println("- " + movie.getTitle() + " (TMDB ID: " + movie.getTmdbId() + ")")
        );
    }

    @AfterAll
    void tearDown() {
        System.out.println("=== 통합 테스트 완료 ===");
    }
}
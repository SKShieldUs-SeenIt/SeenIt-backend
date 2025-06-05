package com.basic.miniPjt5;

import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.service.ContentSearchService;
import com.basic.miniPjt5.service.StatisticsService;
import com.basic.miniPjt5.service.TMDBApiService;
import com.basic.miniPjt5.service.TMDBDataInitializationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// 서비스 레이어 테스트
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional  // 각 테스트 후 롤백
@Rollback       // 명시적 롤백
class ServiceLayerTest {

    @Autowired
    private ContentSearchService contentSearchService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    @MockBean
    private TMDBApiService tmdbApiService;

    @MockBean
    private TMDBDataInitializationService tmdbDataInitializationService;

    @BeforeEach
    void setUp() {
        // 기존 데이터 삭제
        movieRepository.deleteAll();
        genreRepository.deleteAll();

        setupTestData();
    }

    private void setupTestData() {
        // 장르 설정
        Genre actionGenre = Genre.builder().id(28L).name("Action").build();
        Genre comedyGenre = Genre.builder().id(35L).name("Comedy").build();
        genreRepository.saveAll(List.of(actionGenre, comedyGenre));

        // 영화 설정 - 고유한 tmdbId 사용
        List<Movie> movies = List.of(
                Movie.builder()
                        .tmdbId(101L).title("Action Movie 1")  // 고유한 ID
                        .voteAverage(8.0).voteCount(1000)
                        .genres(List.of(actionGenre)).build(),
                Movie.builder()
                        .tmdbId(102L).title("Comedy Movie 1")  // 고유한 ID
                        .voteAverage(7.5).voteCount(800)
                        .genres(List.of(comedyGenre)).build(),
                Movie.builder()
                        .tmdbId(103L).title("Action Comedy Mix")  // 고유한 ID
                        .voteAverage(8.5).voteCount(1200)
                        .genres(List.of(actionGenre, comedyGenre)).build()
        );
        movieRepository.saveAll(movies);
    }
    @Test
    @Order(1)
    @DisplayName("로컬 DB 영화 검색 테스트")
    void testSearchMoviesInDB() {
        // when
        List<Movie> results = contentSearchService.searchMoviesInDB("Action");

        // then
        assertThat(results).hasSize(2); // "Action Movie 1", "Action Comedy Mix"
        assertThat(results).extracting("title")
                .contains("Action Movie 1", "Action Comedy Mix");
        
        System.out.println("'Action' 검색 결과:");
        results.forEach(movie -> System.out.println("- " + movie.getTitle()));
    }

    @Test
    @Order(2)
    @DisplayName("통계 서비스 테스트")
    void testStatisticsService() {
        // when
        Map<String, Object> stats = statisticsService.getContentStatistics();

        // then
        assertThat(stats).isNotNull();
        assertThat(stats.get("totalMovies")).isEqualTo(3L);
        assertThat(stats.get("totalGenres")).isEqualTo(2L);
        
        System.out.println("통계 정보:");
        System.out.println("- 총 영화 수: " + stats.get("totalMovies"));
        System.out.println("- 총 장르 수: " + stats.get("totalGenres"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> genreStats = (Map<String, Long>) stats.get("genreStatistics");
        System.out.println("- 장르별 통계: " + genreStats);
    }

    @Test
    @Order(3)
    @DisplayName("높은 평점 영화 조회 테스트")
    void testGetTopRatedMovies() {
        // when
        List<Movie> topMovies = statisticsService.getTopRatedMovies();

        // then
        assertThat(topMovies).isNotEmpty();
        assertThat(topMovies.get(0).getVoteAverage()).isGreaterThanOrEqualTo(
                topMovies.get(topMovies.size() - 1).getVoteAverage());
        
        System.out.println("평점 높은 영화 순위:");
        for (int i = 0; i < Math.min(topMovies.size(), 3); i++) {
            Movie movie = topMovies.get(i);
            System.out.println((i + 1) + ". " + movie.getTitle() + 
                             " (평점: " + movie.getVoteAverage() + ")");
        }
    }
}

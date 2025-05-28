package com.basic.miniPjt5;

import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.service.ContentSearchService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 성능 테스트를 위한 클래스
@SpringBootTest
@ActiveProfiles("test")
class PerformanceTest {

    @Autowired
    private ContentSearchService contentSearchService;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        // 대량의 테스트 데이터 생성
        List<Movie> movies = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            movies.add(Movie.builder()
                    .tmdbId((long) i)
                    .title("Movie " + i)
                    .voteAverage(5.0 + (Math.random() * 5))
                    .voteCount((int) (Math.random() * 10000))
                    .build());
        }
        movieRepository.saveAll(movies);
    }

    @Test
    @DisplayName("대량 데이터 검색 성능 테스트")
    void testSearchPerformance() {
        // given
        String query = "Movie";

        // when
        long startTime = System.currentTimeMillis();
        List<Movie> results = contentSearchService.searchMoviesInDB(query);
        long endTime = System.currentTimeMillis();

        // then
        long executionTime = endTime - startTime;
        assertThat(results).hasSizeGreaterThan(50);
        assertThat(executionTime).isLessThan(1000); // 1초 이내

        System.out.println("검색 결과 수: " + results.size());
        System.out.println("실행 시간: " + executionTime + "ms");
    }

    @RepeatedTest(5)
    @DisplayName("반복 성능 테스트")
    void testRepeatedSearch(RepetitionInfo repetitionInfo) {
        // given
        String query = "Movie";

        // when
        long startTime = System.nanoTime();
        List<Movie> results = contentSearchService.searchMoviesInDB(query);
        long endTime = System.nanoTime();

        // then
        long executionTimeNs = endTime - startTime;
        double executionTimeMs = executionTimeNs / 1_000_000.0;

        System.out.printf("반복 %d/%d: 실행시간 %.2fms, 결과 수 %d%n",
                repetitionInfo.getCurrentRepetition(),
                repetitionInfo.getTotalRepetitions(),
                executionTimeMs,
                results.size());

        assertThat(results).isNotEmpty();
        assertThat(executionTimeMs).isLessThan(500); // 500ms 이내
    }
}
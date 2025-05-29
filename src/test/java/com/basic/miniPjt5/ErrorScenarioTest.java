package com.basic.miniPjt5;

import com.basic.miniPjt5.DTO.TMDBMovie;
import com.basic.miniPjt5.DTO.TMDBMovieResponse;
import com.basic.miniPjt5.config.TestConfig;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.exception.TMDBApiException;
import com.basic.miniPjt5.service.ContentSearchService;
import com.basic.miniPjt5.service.TMDBApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// 에러 시나리오 테스트
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class ErrorScenarioTest {

    @MockBean  // @Autowired 대신 @MockBean 사용
    private TMDBApiService tmdbApiService;

    @Autowired
    private ContentSearchService contentSearchService;

    @Test
    @DisplayName("TMDB API 호출 실패 시나리오 테스트")
    void testTMDBApiFailure() {
        // given
        String query = "test";
        when(tmdbApiService.searchMovies(query, 1))
                .thenThrow(new TMDBApiException("API 호출 실패"));

        // when & then
        List<Movie> result = contentSearchService.searchAndSaveMovies(query, 1);

        // API 실패해도 빈 리스트 반환해야 함
        assertThat(result).isEmpty();

        System.out.println("API 실패 시 결과: " + result.size());
    }

    @Test
    @DisplayName("잘못된 데이터 처리 테스트")
    void testInvalidDataHandling() {
        // given
        TMDBMovieResponse response = new TMDBMovieResponse();
        TMDBMovie invalidMovie = new TMDBMovie();
        invalidMovie.setId(null); // 잘못된 데이터
        invalidMovie.setTitle("");
        response.setResults(List.of(invalidMovie));

        when(tmdbApiService.searchMovies("test", 1)).thenReturn(response);

        // when
        List<Movie> result = contentSearchService.searchAndSaveMovies("test", 1);

        // then
        assertThat(result).isEmpty(); // 잘못된 데이터는 저장되지 않음

        System.out.println("잘못된 데이터 처리 결과: " + result.size());
    }
}
package com.basic.miniPjt5;

import com.basic.miniPjt5.DTO.ContentDTO;
import com.basic.miniPjt5.DTO.MovieDTO;
import com.basic.miniPjt5.controller.ContentController;
import com.basic.miniPjt5.service.ContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ContentControllerSimpleTest {

    @Mock
    private ContentService contentService;  // ContentSearchService가 아닌 ContentService

    @InjectMocks
    private ContentController contentController;

    @Test
    @DisplayName("영화 검색 컨트롤러 단위 테스트 - GET 방식")
    void testSearchContentByQuery() {
        // given
        String query = "avengers";
        String contentType = "MOVIE";
        boolean useApi = false;
        int page = 0;
        int size = 20;

        // Mock 검색 결과 생성
        ContentDTO.SearchResult mockSearchResult = createMockSearchResult();

        when(contentService.searchContent(any(ContentDTO.SearchRequest.class), anyInt(), anyInt()))
                .thenReturn(mockSearchResult);

        // when
        ResponseEntity<ContentDTO.SearchResult> response = contentController.searchContentByQuery(
                query, contentType, useApi, page, size);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMovies()).hasSize(2);
        assertThat(response.getBody().getQuery()).isEqualTo("avengers");
        assertThat(response.getBody().getTotalMovieResults()).isEqualTo(2);

        // verify - searchContent 메서드가 올바른 파라미터로 호출되었는지 확인
        verify(contentService).searchContent(any(ContentDTO.SearchRequest.class), eq(page), eq(size));
    }

    @Test
    @DisplayName("통합 검색 컨트롤러 단위 테스트 - POST 방식")
    void testSearchContent() {
        // given
        ContentDTO.SearchRequest searchRequest = ContentDTO.SearchRequest.builder()
                .query("avengers")
                .contentType("ALL")
                .useApi(false)
                .sortBy("popularity")
                .sortDirection("desc")
                .build();

        int page = 0;
        int size = 20;

        ContentDTO.SearchResult mockSearchResult = createMockSearchResult();

        when(contentService.searchContent(any(ContentDTO.SearchRequest.class), anyInt(), anyInt()))
                .thenReturn(mockSearchResult);

        // when
        ResponseEntity<ContentDTO.SearchResult> response = contentController.searchContent(
                searchRequest, page, size);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMovies()).hasSize(2);
        assertThat(response.getBody().getTotalResults()).isEqualTo(2);

        verify(contentService).searchContent(searchRequest, page, size);
    }

    // Mock 데이터 생성 헬퍼 메서드
    private ContentDTO.SearchResult createMockSearchResult() {
        List<MovieDTO.ListResponse> movies = new ArrayList<>();

        MovieDTO.ListResponse movie1 = MovieDTO.ListResponse.builder()
                .id(1L)
                .tmdbId(299534L)
                .title("Avengers: Endgame")
                .posterPath("/poster1.jpg")
                .releaseDate("2019-04-26")
                .voteAverage(8.4)
                .voteCount(15000)
                .genreNames(List.of("액션", "모험", "SF"))
                .reviewCount(150)
                .userAverageRating(8.7)
                .build();

        MovieDTO.ListResponse movie2 = MovieDTO.ListResponse.builder()
                .id(2L)
                .tmdbId(299536L)
                .title("Avengers: Infinity War")
                .posterPath("/poster2.jpg")
                .releaseDate("2018-04-27")
                .voteAverage(8.3)
                .voteCount(12000)
                .genreNames(List.of("액션", "모험", "SF"))
                .reviewCount(120)
                .userAverageRating(8.5)
                .build();

        movies.add(movie1);
        movies.add(movie2);

        return ContentDTO.SearchResult.builder()
                .movies(movies)
                .dramas(new ArrayList<>())
                .query("avengers")
                .totalMovieResults(2)
                .totalDramaResults(0)
                .totalResults(2)
                .currentPage(0)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}
package com.basic.miniPjt5;

import com.basic.miniPjt5.controller.ContentController;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.service.ContentSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ContentControllerSimpleTest {

    @Mock
    private ContentSearchService contentSearchService;

    @InjectMocks
    private ContentController contentController;

    @Test
    @DisplayName("영화 검색 컨트롤러 단위 테스트")
    void testSearchMovies() {
        // given
        String query = "avengers";
        int page = 1;
        boolean useApi = false;
        List<Movie> mockMovies = TestDataBuilder.createMockMovies();

        when(contentSearchService.searchMoviesInDB(query)).thenReturn(mockMovies);

        // when
        ResponseEntity<List<Movie>> response = contentController.searchMovies(query, page, useApi);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getTitle()).contains("Avengers");

        verify(contentSearchService).searchMoviesInDB(query);
    }
}
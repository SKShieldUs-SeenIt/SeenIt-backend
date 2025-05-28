package com.basic.miniPjt5;

import com.basic.miniPjt5.config.TestConfig;
import com.basic.miniPjt5.controller.ContentController;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.service.ContentSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Web Layer 테스트
@WebMvcTest(ContentController.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContentSearchService contentSearchService;  // Mock으로 주입됨

    @Test
    @DisplayName("영화 검색 API 테스트")
    void testSearchMoviesAPI() throws Exception {
        // given
        List<Movie> mockMovies = TestDataBuilder.createMockMovies();
        when(contentSearchService.searchMoviesInDB("avengers")).thenReturn(mockMovies);

        // when & then
        mockMvc.perform(get("/api/content/movies/search")
                        .param("query", "avengers")
                        .param("useApi", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", containsString("Avengers")));

        verify(contentSearchService).searchMoviesInDB("avengers");
    }
}
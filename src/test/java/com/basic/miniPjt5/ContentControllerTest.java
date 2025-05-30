package com.basic.miniPjt5;

import com.basic.miniPjt5.controller.ContentController;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.service.ContentSearchService;
import com.basic.miniPjt5.service.TMDBApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = {ContentController.class})
@AutoConfigureWebMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = {ContentController.class, ContentControllerTest.TestWebConfig.class})
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContentSearchService contentSearchService;

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private DramaRepository dramaRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private TMDBApiService tmdbApiService;

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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", containsString("Avengers")));

        verify(contentSearchService).searchMoviesInDB("avengers");
    }

    // 테스트용 CORS 설정 - CORS를 완전히 비활성화
    @Configuration
    static class TestWebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            // CORS 설정을 아예 하지 않음 - 테스트에서는 불필요
        }
    }
}
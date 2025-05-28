package com.basic.miniPjt5;

import com.basic.miniPjt5.DTO.TMDBMovieResponse;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.service.ContentSearchService;
import com.basic.miniPjt5.service.TMDBApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ContentSearchServiceUnitTest {

    @Mock  // Mockito의 @Mock 사용
    private TMDBApiService tmdbApiService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private DramaRepository dramaRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks  // Mock들을 주입받는 실제 객체
    private ContentSearchService contentSearchService;

    @Test
    @DisplayName("로컬 DB 영화 검색 단위 테스트")
    void testSearchMoviesInDB() {
        // given
        String query = "avengers";
        List<Movie> mockMovies = TestDataBuilder.createMockMovies();
        
        when(movieRepository.findByTitleContaining(query)).thenReturn(mockMovies);

        // when
        List<Movie> result = contentSearchService.searchMoviesInDB(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).contains("Avengers");
        verify(movieRepository).findByTitleContaining(query);
        
        System.out.println("단위 테스트 결과 수: " + result.size());
    }

    @Test
    @DisplayName("TMDB API 검색 단위 테스트")
    void testSearchAndSaveMovies() {
        // given
        String query = "test";
        int page = 1;
        
        TMDBMovieResponse mockResponse = TestDataBuilder.createMockTMDBMovieResponse();
        Genre mockGenre = TestDataBuilder.createMockGenre();
        Movie mockSavedMovie = TestDataBuilder.createMockMovie();

        when(tmdbApiService.searchMovies(query, page)).thenReturn(mockResponse);
        when(genreRepository.findById(anyLong())).thenReturn(Optional.of(mockGenre));
        when(movieRepository.findByTmdbId(anyLong())).thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class))).thenReturn(mockSavedMovie);

        // when
        List<Movie> result = contentSearchService.searchAndSaveMovies(query, page);

        // then
        assertThat(result).hasSize(1);
        verify(tmdbApiService).searchMovies(query, page);
        verify(movieRepository).save(any(Movie.class));
        
        System.out.println("API 검색 결과: " + result.get(0).getTitle());
    }
}
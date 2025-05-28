package com.basic.miniPjt5;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Repository 테스트
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private DramaRepository dramaRepository;

    @Test
    @Order(1)
    @DisplayName("장르 저장 및 조회 테스트")
    void testGenreRepository() {
        // given
        Genre genre = Genre.builder()
                .id(28L)
                .name("Action")
                .build();

        // when
        Genre savedGenre = genreRepository.save(genre);

        // then
        assertThat(savedGenre.getId()).isEqualTo(28L);
        assertThat(savedGenre.getName()).isEqualTo("Action");

        Optional<Genre> foundGenre = genreRepository.findById(28L);
        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getName()).isEqualTo("Action");

        System.out.println("저장된 장르: " + savedGenre.getName());
    }

    @Test
    @Order(2)
    @DisplayName("영화 저장 및 조회 테스트")
    void testMovieRepository() {
        // given - 장르 먼저 저장
        Genre actionGenre = Genre.builder()
                .id(28L)
                .name("Action")
                .build();
        genreRepository.save(actionGenre);

        Movie movie = Movie.builder()
                .tmdbId(550L)
                .title("Fight Club")
                .overview("An insomniac office worker...")
                .releaseDate("1999-10-15")
                .posterPath("/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg")
                .voteAverage(8.4)
                .voteCount(26279)
                .genres(List.of(actionGenre))
                .build();

        // when
        Movie savedMovie = movieRepository.save(movie);

        // then
        assertThat(savedMovie.getTmdbId()).isEqualTo(550L);
        assertThat(savedMovie.getTitle()).isEqualTo("Fight Club");
        assertThat(savedMovie.getGenres()).hasSize(1);
        assertThat(savedMovie.getGenres().get(0).getName()).isEqualTo("Action");

        // TMDB ID로 조회 테스트
        Optional<Movie> foundMovie = movieRepository.findByTmdbId(550L);
        assertThat(foundMovie).isPresent();
        assertThat(foundMovie.get().getTitle()).isEqualTo("Fight Club");

        System.out.println("저장된 영화: " + savedMovie.getTitle());
        System.out.println("영화 장르: " + savedMovie.getGenres().get(0).getName());
    }

    @Test
    @Order(3)
    @DisplayName("드라마 저장 및 조회 테스트")
    void testDramaRepository() {
        // given
        Genre dramaGenre = Genre.builder()
                .id(18L)
                .name("Drama")
                .build();
        genreRepository.save(dramaGenre);

        Drama drama = Drama.builder()
                .tmdbId(1399L)
                .title("Game of Thrones")
                .overview("Seven noble families fight...")
                .firstAirDate("2011-04-17")
                .posterPath("/u3bZgnGQ9T01sWNhyveQz0wH0Hl.jpg")
                .numberOfSeasons(8)
                .numberOfEpisodes(73)
                .voteAverage(9.3)
                .voteCount(11504)
                .genres(List.of(dramaGenre))
                .build();

        // when
        Drama savedDrama = dramaRepository.save(drama);

        // then
        assertThat(savedDrama.getTmdbId()).isEqualTo(1399L);
        assertThat(savedDrama.getTitle()).isEqualTo("Game of Thrones");
        assertThat(savedDrama.getNumberOfSeasons()).isEqualTo(8);

        System.out.println("저장된 드라마: " + savedDrama.getTitle());
        System.out.println("시즌 수: " + savedDrama.getNumberOfSeasons());
    }

    @Test
    @Order(4)
    @DisplayName("영화 제목 검색 테스트")
    void testMovieTitleSearch() {
        // given - 테스트 데이터 생성
        Movie movie1 = Movie.builder()
                .tmdbId(100L)
                .title("The Avengers")
                .voteAverage(8.0)
                .voteCount(1000)
                .build();

        Movie movie2 = Movie.builder()
                .tmdbId(101L)
                .title("Avengers: Endgame")
                .voteAverage(8.4)
                .voteCount(2000)
                .build();

        movieRepository.saveAll(List.of(movie1, movie2));

        // when
        List<Movie> results = movieRepository.findByTitleContainingIgnoreCase("avengers");

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("title")
                .containsExactlyInAnyOrder("The Avengers", "Avengers: Endgame");

        System.out.println("'avengers' 검색 결과:");
        results.forEach(movie -> System.out.println("- " + movie.getTitle()));
    }
}
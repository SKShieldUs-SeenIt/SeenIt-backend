package com.basic.miniPjt5.service;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;

import com.basic.miniPjt5.repository.*;
import com.basic.miniPjt5.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TMDBDataInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(TMDBDataInitializationService.class);

    private final TMDBApiService tmdbApiService;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;

    public TMDBDataInitializationService(TMDBApiService tmdbApiService,
                                       GenreRepository genreRepository,
                                       MovieRepository movieRepository,
                                       DramaRepository dramaRepository) {
        this.tmdbApiService = tmdbApiService;
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
        this.dramaRepository = dramaRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        try {
            logger.info("TMDB 데이터 초기화 시작");

            // 1. 장르 데이터 초기화
            initializeGenres();

            // 2. 영화 데이터 초기화 (인기순, 평점순)
            initializeMovies();

            // 3. 드라마 데이터 초기화 (인기순, 평점순)
            initializeDramas();

            logger.info("TMDB 데이터 초기화 완료");

        } catch (Exception e) {
            logger.error("TMDB 데이터 초기화 실패", e);
        }
    }

    private void initializeGenres() {
        logger.info("장르 데이터 초기화 시작");

        // 영화 장르
        TMDBGenreResponse movieGenres = tmdbApiService.getMovieGenres();
        if (movieGenres != null && movieGenres.getGenres() != null) {
            for (TMDBGenre tmdbGenre : movieGenres.getGenres()) {
                saveGenreIfNotExists(tmdbGenre, "MOVIE");
            }
        }

        // TV 장르
        TMDBGenreResponse tvGenres = tmdbApiService.getTVGenres();
        if (tvGenres != null && tvGenres.getGenres() != null) {
            for (TMDBGenre tmdbGenre : tvGenres.getGenres()) {
                saveGenreIfNotExists(tmdbGenre, "TV");
            }
        }

        logger.info("장르 데이터 초기화 완료");
    }

    private void saveGenreIfNotExists(TMDBGenre tmdbGenre, String type) {
        // TMDB ID를 직접 사용하여 장르 저장
        if (!genreRepository.existsById(tmdbGenre.getId().longValue())) {
            Genre genre = Genre.builder()
                    .id(tmdbGenre.getId().longValue())
                    .name(tmdbGenre.getName())
                    .build();
            genreRepository.save(genre);
            logger.info("새 장르 저장: {} (ID: {})", genre.getName(), genre.getId());
        }
    }

    private void initializeMovies() {
        logger.info("영화 데이터 초기화 시작");

        // 인기 영화 가져오기 (처음 5페이지)
        for (int page = 1; page <= 5; page++) {
            TMDBMovieResponse response = tmdbApiService.getPopularMovies(page);
            if (response != null && response.getResults() != null) {
                for (TMDBMovie tmdbMovie : response.getResults()) {
                    saveMovieIfNotExists(tmdbMovie);
                }
            }

            // API 호출 제한을 위한 딜레이
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 평점 높은 영화 가져오기 (처음 3페이지)
        for (int page = 1; page <= 3; page++) {
            TMDBMovieResponse response = tmdbApiService.getTopRatedMovies(page);
            if (response != null && response.getResults() != null) {
                for (TMDBMovie tmdbMovie : response.getResults()) {
                    saveMovieIfNotExists(tmdbMovie);
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("영화 데이터 초기화 완료");
    }

    private void saveMovieIfNotExists(TMDBMovie tmdbMovie) {
        if (!movieRepository.existsByTmdbId(tmdbMovie.getId())) {
            Movie movie = Movie.builder()
                    .tmdbId(tmdbMovie.getId())
                    .title(tmdbMovie.getTitle())
                    .overview(tmdbMovie.getOverview())
                    .releaseDate(tmdbMovie.getReleaseDate())
                    .posterPath(tmdbMovie.getPosterPath())
                    .voteAverage(tmdbMovie.getVoteAverage() != null ? tmdbMovie.getVoteAverage() : 0.0)
                    .voteCount(tmdbMovie.getVoteCount() != null ? tmdbMovie.getVoteCount() : 0)
                    .build();

            // 장르 연결
            if (tmdbMovie.getGenreIds() != null && !tmdbMovie.getGenreIds().isEmpty()) {
                List<Genre> genres = new ArrayList<>();
                for (Integer genreId : tmdbMovie.getGenreIds()) {
                    genreRepository.findById(genreId.longValue()).ifPresent(genres::add);
                }
                movie.setGenres(genres);
            }

            movieRepository.save(movie);
            logger.info("새 영화 저장: {} (TMDB ID: {})", movie.getTitle(), movie.getTmdbId());
        }
    }

    private void initializeDramas() {
        logger.info("드라마 데이터 초기화 시작");

        // 인기 드라마 가져오기 (처음 5페이지)
        for (int page = 1; page <= 5; page++) {
            TMDBTVResponse response = tmdbApiService.getPopularTVShows(page);
            if (response != null && response.getResults() != null) {
                for (TMDBDrama tmdbDrama : response.getResults()) {
                    saveDramaIfNotExists(tmdbDrama);
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 평점 높은 드라마 가져오기 (처음 3페이지)
        for (int page = 1; page <= 3; page++) {
            TMDBTVResponse response = tmdbApiService.getTopRatedTVShows(page);
            if (response != null && response.getResults() != null) {
                for (TMDBDrama tmdbDrama : response.getResults()) {
                    saveDramaIfNotExists(tmdbDrama);
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("드라마 데이터 초기화 완료");
    }

    private void saveDramaIfNotExists(TMDBDrama tmdbDrama) {
        if (!dramaRepository.existsByTmdbId(tmdbDrama.getId())) {
            Drama drama = Drama.builder()
                    .tmdbId(tmdbDrama.getId())
                    .title(tmdbDrama.getName())
                    .overview(tmdbDrama.getOverview())
                    .firstAirDate(tmdbDrama.getFirstAirDate())
                    .posterPath(tmdbDrama.getPosterPath())
                    .voteAverage(tmdbDrama.getVoteAverage() != null ? tmdbDrama.getVoteAverage() : 0.0)
                    .voteCount(tmdbDrama.getVoteCount() != null ? tmdbDrama.getVoteCount() : 0)
                    .numberOfSeasons(tmdbDrama.getNumberOfSeasons())
                    .numberOfEpisodes(tmdbDrama.getNumberOfEpisodes())
                    .build();

            // 장르 연결
            if (tmdbDrama.getGenreIds() != null && !tmdbDrama.getGenreIds().isEmpty()) {
                List<Genre> genres = new ArrayList<>();
                for (Integer genreId : tmdbDrama.getGenreIds()) {
                    genreRepository.findById(genreId.longValue()).ifPresent(genres::add);
                }
                drama.setGenres(genres);
            }

            dramaRepository.save(drama);
            logger.info("새 드라마 저장: {} (TMDB ID: {})", drama.getTitle(), drama.getTmdbId());
        }
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString);
        } catch (Exception e) {
            logger.warn("날짜 파싱 실패: {}", dateString);
            return null;
        }
    }
}
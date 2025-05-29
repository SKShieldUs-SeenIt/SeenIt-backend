package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.*;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ContentSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContentSearchService.class);
    
    private final TMDBApiService tmdbApiService;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;
    private final GenreRepository genreRepository;
    
    public ContentSearchService(TMDBApiService tmdbApiService,
                              MovieRepository movieRepository,
                              DramaRepository dramaRepository,
                              GenreRepository genreRepository) {
        this.tmdbApiService = tmdbApiService;
        this.movieRepository = movieRepository;
        this.dramaRepository = dramaRepository;
        this.genreRepository = genreRepository;
    }
    
    // ========== 로컬 DB 검색 메서드들 ==========
    
    /**
     * 로컬 DB에서 영화 검색
     */
    public List<Movie> searchMoviesInDB(String query) {
        if (query == null || query.trim().length() == 0) {
            return new ArrayList<>();
        }
        
        try {
            // 대소문자 구분 없이 검색
            return movieRepository.findByTitleContainingIgnoreCase(query.trim());
        } catch (Exception e) {
            logger.error("로컬 DB 영화 검색 실패: {}", query, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 로컬 DB에서 드라마 검색
     */
    public List<Drama> searchDramasInDB(String query) {
        if (query == null || query.trim().length() == 0) {
            return new ArrayList<>();
        }
        
        try {
            return dramaRepository.findByTitleContainingIgnoreCase(query.trim());
        } catch (Exception e) {
            logger.error("로컬 DB 드라마 검색 실패: {}", query, e);
            return new ArrayList<>();
        }
    }
    
    // ========== TMDB API 검색 및 저장 메서드들 ==========
    
    /**
     * TMDB API로 영화 검색 및 DB 저장
     */
    @Transactional
    public List<Movie> searchAndSaveMovies(String query, int page) {
        if (query == null || query.trim().length() == 0) {
            return new ArrayList<>();
        }
        
        try {
            TMDBMovieResponse response = tmdbApiService.searchMovies(query.trim(), page);
            List<Movie> movies = new ArrayList<>();
            
            if (response != null && response.getResults() != null) {
                for (TMDBMovie tmdbMovie : response.getResults()) {
                    Movie movie = findOrSaveMovie(tmdbMovie);
                    if (movie != null) {
                        movies.add(movie);
                    }
                }
            }
            
            logger.info("TMDB API 영화 검색 완료: 검색어={}, 결과수={}", query, movies.size());
            return movies;
            
        } catch (Exception e) {
            logger.error("영화 검색 및 저장 실패: {}", query, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * TMDB API로 드라마 검색 및 DB 저장
     */
    @Transactional
    public List<Drama> searchAndSaveDramas(String query, int page) {
        if (query == null || query.trim().length() == 0) {
            return new ArrayList<>();
        }
        
        try {
            TMDBTVResponse response = tmdbApiService.searchTVShows(query.trim(), page);
            List<Drama> dramas = new ArrayList<>();
            
            if (response != null && response.getResults() != null) {
                for (TMDBDrama tmdbDrama : response.getResults()) {
                    Drama drama = findOrSaveDrama(tmdbDrama);
                    if (drama != null) {
                        dramas.add(drama);
                    }
                }
            }
            
            logger.info("TMDB API 드라마 검색 완료: 검색어={}, 결과수={}", query, dramas.size());
            return dramas;
            
        } catch (Exception e) {
            logger.error("드라마 검색 및 저장 실패: {}", query, e);
            return new ArrayList<>();
        }
    }
    
    // ========== 내부 헬퍼 메서드들 ==========
    
    /**
     * 영화 찾기 또는 새로 저장
     */
    private Movie findOrSaveMovie(TMDBMovie tmdbMovie) {
        if (tmdbMovie == null || tmdbMovie.getId() == null) {
            logger.warn("유효하지 않은 TMDB 영화 데이터: {}", tmdbMovie);
            return null;
        }
        
        // 기존 영화 확인
        Optional<Movie> existingMovie = movieRepository.findByTmdbId(tmdbMovie.getId());
        if (existingMovie.isPresent()) {
            return existingMovie.get();
        }
        
        // 새 영화 저장
        try {
            Movie movie = Movie.builder()
                    .tmdbId(tmdbMovie.getId())
                    .title(tmdbMovie.getTitle() != null ? tmdbMovie.getTitle() : "제목 없음")
                    .overview(tmdbMovie.getOverview())
                    .releaseDate(tmdbMovie.getReleaseDate())
                    .posterPath(tmdbMovie.getPosterPath())
                    .voteAverage(tmdbMovie.getVoteAverage() != null ? tmdbMovie.getVoteAverage() : 0.0)
                    .voteCount(tmdbMovie.getVoteCount() != null ? tmdbMovie.getVoteCount() : 0)
                    .build();
            
            // 장르 연결
            if (tmdbMovie.getGenreIds() != null && tmdbMovie.getGenreIds().size() > 0) {
                List<Genre> genres = new ArrayList<>();
                for (Integer genreId : tmdbMovie.getGenreIds()) {
                    genreRepository.findById(genreId.longValue()).ifPresent(genres::add);
                }
                movie.setGenres(genres);
            }
            
            Movie savedMovie = movieRepository.save(movie);
            logger.debug("새 영화 저장 완료: {} (TMDB ID: {})", savedMovie.getTitle(), savedMovie.getTmdbId());
            return savedMovie;
            
        } catch (Exception e) {
            logger.error("영화 저장 실패: TMDB ID={}", tmdbMovie.getId(), e);
            return null;
        }
    }
    
    /**
     * 드라마 찾기 또는 새로 저장
     */
    private Drama findOrSaveDrama(TMDBDrama tmdbDrama) {
        if (tmdbDrama == null || tmdbDrama.getId() == null) {
            logger.warn("유효하지 않은 TMDB TV 데이터: {}", tmdbDrama);
            return null;
        }
        
        // 기존 드라마 확인
        Optional<Drama> existingDrama = dramaRepository.findByTmdbId(tmdbDrama.getId());
        if (existingDrama.isPresent()) {
            return existingDrama.get();
        }
        
        // 새 드라마 저장
        try {
            Drama drama = Drama.builder()
                    .tmdbId(tmdbDrama.getId())
                    .title(tmdbDrama.getName() != null ? tmdbDrama.getName() : "제목 없음")
                    .overview(tmdbDrama.getOverview())
                    .firstAirDate(tmdbDrama.getFirstAirDate())
                    .posterPath(tmdbDrama.getPosterPath())
                    .voteAverage(tmdbDrama.getVoteAverage() != null ? tmdbDrama.getVoteAverage() : 0.0)
                    .voteCount(tmdbDrama.getVoteCount() != null ? tmdbDrama.getVoteCount() : 0)
                    .numberOfSeasons(tmdbDrama.getNumberOfSeasons())
                    .numberOfEpisodes(tmdbDrama.getNumberOfEpisodes())
                    .build();
            
            // 장르 연결
            if (tmdbDrama.getGenreIds() != null && tmdbDrama.getGenreIds().size() > 0) {
                List<Genre> genres = new ArrayList<>();
                for (Integer genreId : tmdbDrama.getGenreIds()) {
                    genreRepository.findById(genreId.longValue()).ifPresent(genres::add);
                }
                drama.setGenres(genres);
            }
            
            Drama savedDrama = dramaRepository.save(drama);
            logger.debug("새 드라마 저장 완료: {} (TMDB ID: {})", savedDrama.getTitle(), savedDrama.getTmdbId());
            return savedDrama;
            
        } catch (Exception e) {
            logger.error("드라마 저장 실패: TMDB ID={}", tmdbDrama.getId(), e);
            return null;
        }
    }
    
    // ========== 추가 유틸리티 메서드들 ==========
    
    /**
     * 통합 검색 (로컬 DB 우선, 부족하면 API 호출)
     */
    @Transactional
    public SearchResultDTO searchContent(String query, int page) {
        List<Movie> movies = new ArrayList<>();
        List<Drama> dramas = new ArrayList<>();
        
        // 1. 로컬 DB에서 먼저 검색
        List<Movie> localMovies = searchMoviesInDB(query);
        List<Drama> localDramas = searchDramasInDB(query);
        
        movies.addAll(localMovies);
        dramas.addAll(localDramas);
        
        // 2. 결과가 부족하면 API 검색
        if (localMovies.size() < 5) {
            List<Movie> apiMovies = searchAndSaveMovies(query, page);
            // 중복 제거하면서 추가
            for (Movie apiMovie : apiMovies) {
                boolean exists = movies.stream()
                        .anyMatch(m -> m.getTmdbId().equals(apiMovie.getTmdbId()));
                if (!exists) {
                    movies.add(apiMovie);
                }
            }
        }
        
        if (localDramas.size() < 5) {
            List<Drama> apiDramas = searchAndSaveDramas(query, page);
            // 중복 제거하면서 추가
            for (Drama apiDrama : apiDramas) {
                boolean exists = dramas.stream()
                        .anyMatch(d -> d.getTmdbId().equals(apiDrama.getTmdbId()));
                if (!exists) {
                    dramas.add(apiDrama);
                }
            }
        }
        
        return SearchResultDTO.builder()
                .movies(movies)
                .dramas(dramas)
                .query(query)
                .totalResults(movies.size() + dramas.size())
                .build();
    }
    
    /**
     * 장르별 컨텐츠 검색
     */
    public List<Movie> getMoviesByGenre(Long genreId) {
        try {
            return movieRepository.findByGenreId(genreId);
        } catch (Exception e) {
            logger.error("장르별 영화 검색 실패: genreId={}", genreId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 장르별 드라마 검색
     */
    public List<Drama> getDramasByGenre(Long genreId) {
        try {
            return dramaRepository.findByGenreId(genreId);
        } catch (Exception e) {
            logger.error("장르별 드라마 검색 실패: genreId={}", genreId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 인기 컨텐츠 조회
     */
    public List<Movie> getPopularMovies(int limit) {
        try {
            if (limit <= 0) limit = 20;
            List<Movie> movies = movieRepository.findTop20ByOrderByVoteAverageDesc();
            return movies.size() > limit ? movies.subList(0, limit) : movies;
        } catch (Exception e) {
            logger.error("인기 영화 조회 실패", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 평점 높은 드라마 조회
     */
    public List<Drama> getTopRatedDramas(int limit) {
        try {
            if (limit <= 0) limit = 20;
            List<Drama> dramas = dramaRepository.findTop20ByOrderByVoteAverageDesc();
            return dramas.size() > limit ? dramas.subList(0, limit) : dramas;
        } catch (Exception e) {
            logger.error("평점 높은 드라마 조회 실패", e);
            return new ArrayList<>();
        }
    }
}
package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.SearchResultDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.service.ContentSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*") // 프론트엔드와의 CORS 문제 해결
public class ContentController {
    
    private static final Logger logger = LoggerFactory.getLogger(ContentController.class);
    
    private final ContentSearchService contentSearchService;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;
    private final GenreRepository genreRepository;
    
    public ContentController(ContentSearchService contentSearchService,
                           MovieRepository movieRepository,
                           DramaRepository dramaRepository,
                           GenreRepository genreRepository) {
        this.contentSearchService = contentSearchService;
        this.movieRepository = movieRepository;
        this.dramaRepository = dramaRepository;
        this.genreRepository = genreRepository;
    }
    
    // ========== 통합 검색 API ==========
    
    /**
     * 통합 검색 (로컬 DB 우선, 없으면 TMDB API 호출)
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> searchContent(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        
        try {
            if (query == null || query.trim().length() == 0) {
                return ResponseEntity.badRequest().build();
            }
            
            SearchResultDTO result = contentSearchService.searchContent(query.trim(), page);
            logger.info("통합 검색 완료: 검색어={}, 총 결과수={}", query, result.getTotalResults());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("통합 검색 실패: 검색어={}", query, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ========== 영화 관련 API ==========
    
    /**
     * 영화 검색
     */
    @GetMapping("/movies/search")
    public ResponseEntity<List<Movie>> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "false") boolean useApi) {
        
        try {
            if (query == null || query.trim().length() == 0) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Movie> movies;
            if (useApi) {
                movies = contentSearchService.searchAndSaveMovies(query.trim(), page);
                logger.info("TMDB API 영화 검색: 검색어={}, 결과수={}", query, movies.size());
            } else {
                movies = contentSearchService.searchMoviesInDB(query.trim());
                logger.info("로컬 DB 영화 검색: 검색어={}, 결과수={}", query, movies.size());
            }
            
            return ResponseEntity.ok(movies);
            
        } catch (Exception e) {
            logger.error("영화 검색 실패: 검색어={}", query, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 인기 영화 목록
     */
    @GetMapping("/movies/popular")
    public ResponseEntity<List<Movie>> getPopularMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("voteAverage").descending());
            Page<Movie> moviePage = movieRepository.findAll(pageable);
            
            logger.info("인기 영화 조회: 페이지={}, 크기={}, 결과수={}", page, size, moviePage.getContent().size());
            return ResponseEntity.ok(moviePage.getContent());
            
        } catch (Exception e) {
            logger.error("인기 영화 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 영화 상세 정보
     */
    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        try {
            Optional<Movie> movie = movieRepository.findById(id);
            if (movie.isPresent()) {
                logger.info("영화 상세 조회: ID={}, 제목={}", id, movie.get().getTitle());
                return ResponseEntity.ok(movie.get());
            } else {
                logger.warn("영화를 찾을 수 없음: ID={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("영화 상세 조회 실패: ID={}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 평점 높은 영화 TOP 20
     */
    @GetMapping("/movies/top-rated")
    public ResponseEntity<List<Movie>> getTopRatedMovies() {
        try {
            List<Movie> movies = contentSearchService.getPopularMovies(20);
            logger.info("평점 높은 영화 조회: 결과수={}", movies.size());
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            logger.error("평점 높은 영화 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ========== 드라마 관련 API ==========
    
    /**
     * 드라마 검색
     */
    @GetMapping("/dramas/search")
    public ResponseEntity<List<Drama>> searchDramas(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "false") boolean useApi) {
        
        try {
            if (query == null || query.trim().length() == 0) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Drama> dramas;
            if (useApi) {
                dramas = contentSearchService.searchAndSaveDramas(query.trim(), page);
                logger.info("TMDB API 드라마 검색: 검색어={}, 결과수={}", query, dramas.size());
            } else {
                dramas = contentSearchService.searchDramasInDB(query.trim());
                logger.info("로컬 DB 드라마 검색: 검색어={}, 결과수={}", query, dramas.size());
            }
            
            return ResponseEntity.ok(dramas);
            
        } catch (Exception e) {
            logger.error("드라마 검색 실패: 검색어={}", query, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 인기 드라마 목록
     */
    @GetMapping("/dramas/popular")
    public ResponseEntity<List<Drama>> getPopularDramas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("voteAverage").descending());
            Page<Drama> dramaPage = dramaRepository.findAll(pageable);
            
            logger.info("인기 드라마 조회: 페이지={}, 크기={}, 결과수={}", page, size, dramaPage.getContent().size());
            return ResponseEntity.ok(dramaPage.getContent());
            
        } catch (Exception e) {
            logger.error("인기 드라마 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 드라마 상세 정보
     */
    @GetMapping("/dramas/{id}")
    public ResponseEntity<Drama> getDramaById(@PathVariable Long id) {
        try {
            Optional<Drama> drama = dramaRepository.findById(id);
            if (drama.isPresent()) {
                logger.info("드라마 상세 조회: ID={}, 제목={}", id, drama.get().getTitle());
                return ResponseEntity.ok(drama.get());
            } else {
                logger.warn("드라마를 찾을 수 없음: ID={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("드라마 상세 조회 실패: ID={}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 평점 높은 드라마 TOP 20
     */
    @GetMapping("/dramas/top-rated")
    public ResponseEntity<List<Drama>> getTopRatedDramas() {
        try {
            List<Drama> dramas = contentSearchService.getTopRatedDramas(20);
            logger.info("평점 높은 드라마 조회: 결과수={}", dramas.size());
            return ResponseEntity.ok(dramas);
        } catch (Exception e) {
            logger.error("평점 높은 드라마 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ========== 장르 관련 API ==========
    
    /**
     * 모든 장르 목록
     */
    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        try {
            List<Genre> genres = genreRepository.findAll();
            logger.info("장르 목록 조회: 총 {}개", genres.size());
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            logger.error("장르 목록 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 장르별 영화 검색
     */
    @GetMapping("/movies/genre/{genreId}")
    public ResponseEntity<List<Movie>> getMoviesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            List<Movie> movies = contentSearchService.getMoviesByGenre(genreId);
            
            // 페이징 처리
            int start = page * size;
            int end = Math.min(start + size, movies.size());
            
            if (start >= movies.size()) {
                return ResponseEntity.ok(List.of()); // 빈 리스트 반환
            }
            
            List<Movie> pagedMovies = movies.subList(start, end);
            logger.info("장르별 영화 조회: 장르ID={}, 결과수={}", genreId, pagedMovies.size());
            
            return ResponseEntity.ok(pagedMovies);
            
        } catch (Exception e) {
            logger.error("장르별 영화 조회 실패: 장르ID={}", genreId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 장르별 드라마 검색
     */
    @GetMapping("/dramas/genre/{genreId}")
    public ResponseEntity<List<Drama>> getDramasByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            List<Drama> dramas = contentSearchService.getDramasByGenre(genreId);
            
            // 페이징 처리
            int start = page * size;
            int end = Math.min(start + size, dramas.size());
            
            if (start >= dramas.size()) {
                return ResponseEntity.ok(List.of()); // 빈 리스트 반환
            }
            
            List<Drama> pagedDramas = dramas.subList(start, end);
            logger.info("장르별 드라마 조회: 장르ID={}, 결과수={}", genreId, pagedDramas.size());
            
            return ResponseEntity.ok(pagedDramas);
            
        } catch (Exception e) {
            logger.error("장르별 드라마 조회 실패: 장르ID={}", genreId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ========== 통계 및 기타 API ==========
    
    /**
     * 컨텐츠 통계 정보
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getContentStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long totalMovies = movieRepository.count();
            long totalDramas = dramaRepository.count();
            long totalGenres = genreRepository.count();
            
            stats.put("totalMovies", totalMovies);
            stats.put("totalDramas", totalDramas);
            stats.put("totalGenres", totalGenres);
            stats.put("totalContent", totalMovies + totalDramas);
            
            // 장르별 통계
            List<Genre> genres = genreRepository.findAll();
            Map<String, Long> genreStats = new HashMap<>();
            for (Genre genre : genres) {
                long movieCount = genreRepository.countMoviesByGenreId(genre.getId());
                long dramaCount = genreRepository.countDramasByGenreId(genre.getId());
                genreStats.put(genre.getName(), movieCount + dramaCount);
            }
            stats.put("genreStats", genreStats);
            
            logger.info("통계 정보 조회 완료: 영화={}, 드라마={}, 장르={}", totalMovies, totalDramas, totalGenres);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("통계 정보 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 연도별 영화 조회
     */
    @GetMapping("/movies/year/{year}")
    public ResponseEntity<List<Movie>> getMoviesByYear(@PathVariable String year) {
        try {
            List<Movie> movies = movieRepository.findByReleaseYear(year);
            logger.info("연도별 영화 조회: 연도={}, 결과수={}", year, movies.size());
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            logger.error("연도별 영화 조회 실패: 연도={}", year, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 연도별 드라마 조회
     */
    @GetMapping("/dramas/year/{year}")
    public ResponseEntity<List<Drama>> getDramasByYear(@PathVariable String year) {
        try {
            List<Drama> dramas = dramaRepository.findByFirstAirYear(year);
            logger.info("연도별 드라마 조회: 연도={}, 결과수={}", year, dramas.size());
            return ResponseEntity.ok(dramas);
        } catch (Exception e) {
            logger.error("연도별 드라마 조회 실패: 연도={}", year, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ========== 헬스 체크 API ==========
    
    /**
     * API 상태 확인
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // DB 연결 체크
            long movieCount = movieRepository.count();
            long dramaCount = dramaRepository.count();
            long genreCount = genreRepository.count();
            
            health.put("status", "UP");
            health.put("database", "CONNECTED");
            health.put("movieCount", movieCount);
            health.put("dramaCount", dramaCount);
            health.put("genreCount", genreCount);
            health.put("timestamp", System.currentTimeMillis());
            
            logger.info("헬스 체크 완료: 정상 상태");
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("database", "DISCONNECTED");
            health.put("error", e.getMessage());
            health.put("timestamp", System.currentTimeMillis());
            
            logger.error("헬스 체크 실패", e);
            return ResponseEntity.status(500).body(health);
        }
    }
}
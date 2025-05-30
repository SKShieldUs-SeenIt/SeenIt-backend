package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.service.TMDBApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    private final TMDBApiService tmdbApiService;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;
    private final GenreRepository genreRepository;
    
    public HealthController(TMDBApiService tmdbApiService,
                          MovieRepository movieRepository,
                          DramaRepository dramaRepository,
                          GenreRepository genreRepository) {
        this.tmdbApiService = tmdbApiService;
        this.movieRepository = movieRepository;
        this.dramaRepository = dramaRepository;
        this.genreRepository = genreRepository;
    }
    
    @GetMapping
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
            
            // TMDB API 연결 체크 (선택적)
            try {
                tmdbApiService.getMovieGenres();
                health.put("tmdbApi", "CONNECTED");
            } catch (Exception e) {
                health.put("tmdbApi", "DISCONNECTED");
                health.put("tmdbApiError", e.getMessage());
            }
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("database", "DISCONNECTED");
            health.put("error", e.getMessage());
            health.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }
}
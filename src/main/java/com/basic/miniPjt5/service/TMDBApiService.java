package com.basic.miniPjt5.service;

import com.basic.miniPjt5.response.TMDBGenreResponse;
import com.basic.miniPjt5.response.TMDBMovieResponse;
import com.basic.miniPjt5.response.TMDBTVResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class TMDBApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(TMDBApiService.class);
    
    @Value("${tmdb.api.key}")
    private String apiKey;
    
    @Value("${tmdb.api.base-url:https://api.themoviedb.org/3}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    public TMDBApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }
    
    public TMDBMovieResponse getPopularMovies(int page) {
        int tmdbPage = page + 1;
        String url = String.format("%s/movie/popular?api_key=%s&page=%d&language=ko-KR", 
                                 baseUrl, apiKey, tmdbPage);
        
        try {
            return restTemplate.getForObject(url, TMDBMovieResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch popular movies from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
    
    public TMDBMovieResponse getTopRatedMovies(int page) {
        int tmdbPage = page + 1;
        String url = String.format("%s/movie/top_rated?api_key=%s&page=%d&language=ko-KR", 
                                 baseUrl, apiKey, tmdbPage);
        
        try {
            return restTemplate.getForObject(url, TMDBMovieResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch top rated movies from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
    
    public TMDBTVResponse getPopularTVShows(int page) {
        int tmdbPage = page + 1;
        String url = String.format("%s/tv/popular?api_key=%s&page=%d&language=ko-KR", 
                                 baseUrl, apiKey, tmdbPage);
        
        try {
            return restTemplate.getForObject(url, TMDBTVResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch popular TV shows from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
    
    public TMDBTVResponse getTopRatedTVShows(int page) {
        int tmdbPage = page + 1;
        String url = String.format("%s/tv/top_rated?api_key=%s&page=%d&language=ko-KR", 
                                 baseUrl, apiKey, tmdbPage);
        
        try {
            return restTemplate.getForObject(url, TMDBTVResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch top rated TV shows from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
    
    public TMDBGenreResponse getMovieGenres() {
        String url = String.format("%s/genre/movie/list?api_key=%s&language=ko-KR", 
                                 baseUrl, apiKey);
        
        try {
            return restTemplate.getForObject(url, TMDBGenreResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch movie genres from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
    
    public TMDBGenreResponse getTVGenres() {
        String url = String.format("%s/genre/tv/list?api_key=%s&language=ko-KR", 
                                 baseUrl, apiKey);
        
        try {
            return restTemplate.getForObject(url, TMDBGenreResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch TV genres from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
    
    public TMDBMovieResponse searchMovies(String query, int page) {
        int tmdbPage = page + 1;
        String url = String.format("%s/search/movie?api_key=%s&query=%s&page=%d&language=ko-KR", 
                                 baseUrl, apiKey, query, tmdbPage);
        
        try {
            return restTemplate.getForObject(url, TMDBMovieResponse.class);
        } catch (Exception e) {
            logger.error("Failed to search movies from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
    
    public TMDBTVResponse searchTVShows(String query, int page) {
        int tmdbPage = page + 1;
        String url = String.format("%s/search/tv?api_key=%s&query=%s&page=%d&language=ko-KR", 
                                 baseUrl, apiKey, query, tmdbPage);
        
        try {
            return restTemplate.getForObject(url, TMDBTVResponse.class);
        } catch (Exception e) {
            logger.error("Failed to search TV shows from TMDB API", e);
            throw new RuntimeException("TMDB API 호출 실패", e);
        }
    }
}
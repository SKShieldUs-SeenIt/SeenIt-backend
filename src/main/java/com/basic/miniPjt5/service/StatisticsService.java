package com.basic.miniPjt5.service;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatisticsService {
    
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;
    private final GenreRepository genreRepository;
    
    public StatisticsService(MovieRepository movieRepository,
                           DramaRepository dramaRepository,
                           GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.dramaRepository = dramaRepository;
        this.genreRepository = genreRepository;
    }
    
    public Map<String, Object> getContentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 기본 통계
        stats.put("totalMovies", movieRepository.count());
        stats.put("totalDramas", dramaRepository.count());
        stats.put("totalGenres", genreRepository.count());
        
        // 장르별 통계
        List<Genre> genres = genreRepository.findAll();
        Map<String, Long> genreStats = new HashMap<>();
        
        for (Genre genre : genres) {
            Long movieCount = genreRepository.countMoviesByGenreId(genre.getId());
            Long dramaCount = genreRepository.countDramasByGenreId(genre.getId());
            genreStats.put(genre.getName(), movieCount + dramaCount);
        }
        
        stats.put("genreStatistics", genreStats);
        
        return stats;
    }
    
    @Cacheable(value = "topRatedMovies")
    public List<Movie> getTopRatedMovies() {
        return movieRepository.findTop20ByOrderByVoteAverageDesc();
    }
    
    @Cacheable(value = "topRatedDramas")
    public List<Drama> getTopRatedDramas() {
        return dramaRepository.findTop20ByOrderByVoteAverageDesc();
    }
}
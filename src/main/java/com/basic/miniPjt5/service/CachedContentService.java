package com.basic.miniPjt5.service;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 캐시를 활용한 서비스 메서드들 추가
@Service
@Transactional(readOnly = true)
public class CachedContentService {
    
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;
    private final GenreRepository genreRepository;
    
    public CachedContentService(MovieRepository movieRepository,
                              DramaRepository dramaRepository,
                              GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.dramaRepository = dramaRepository;
        this.genreRepository = genreRepository;
    }
    
    @Cacheable(value = "popularMovies", key = "#page + '_' + #size")
    public Page<Movie> getPopularMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("voteAverage").descending());
        return movieRepository.findAll(pageable);
    }
    
    @Cacheable(value = "popularDramas", key = "#page + '_' + #size")
    public Page<Drama> getPopularDramas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("voteAverage").descending());
        return dramaRepository.findAll(pageable);
    }
    
    @Cacheable(value = "genres")
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
    
    @Cacheable(value = "moviesByGenre", key = "#genreId + '_' + #page + '_' + #size")
    public Page<Movie> getMoviesByGenre(Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("voteAverage").descending());
        return movieRepository.findByGenreId(genreId, pageable);
    }
    
    @Cacheable(value = "dramasByGenre", key = "#genreId + '_' + #page + '_' + #size")
    public Page<Drama> getDramasByGenre(Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("voteAverage").descending());
        return dramaRepository.findByGenres_Id(genreId, pageable);
    }
    
    @CacheEvict(value = {"popularMovies", "popularDramas", "moviesByGenre", "dramasByGenre"}, allEntries = true)
    public void clearCache() {
        // 캐시 무효화 메서드
    }
}
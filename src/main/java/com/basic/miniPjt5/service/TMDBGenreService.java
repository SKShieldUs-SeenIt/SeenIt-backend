package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.GenreDTO;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.mapper.GenreMapper;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.response.TMDBGenre;
import com.basic.miniPjt5.response.TMDBGenreResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TMDBGenreService {

    private static final String TMDB_MOVIE_GENRES_URL = "/genre/movie/list";
    private static final String TMDB_TV_GENRES_URL = "/genre/tv/list";
    
    @Value("${tmdb.api.key}")
    private String apiKey;
    
    @Value("${tmdb.api.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    
    public TMDBGenreService(RestTemplateBuilder restTemplateBuilder,
                            GenreRepository genreRepository,
                            GenreMapper genreMapper) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    // TMDB에서 영화 장르 가져오기
    public TMDBGenreResponse fetchMovieGenresFromTMDB() {
        String url = String.format("%s%s?api_key=%s&language=ko-KR", 
                                 baseUrl, TMDB_MOVIE_GENRES_URL, apiKey);
        
        try {
            return restTemplate.getForObject(url, TMDBGenreResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("TMDB 영화 장르 조회 실패", e);
        }
    }

    // TMDB에서 TV 장르 가져오기
    public TMDBGenreResponse fetchTVGenresFromTMDB() {
        String url = String.format("%s%s?api_key=%s&language=ko-KR", 
                                 baseUrl, TMDB_TV_GENRES_URL, apiKey);
        
        try {
            return restTemplate.getForObject(url, TMDBGenreResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("TMDB TV 장르 조회 실패", e);
        }
    }

    // TMDB에서 모든 장르 가져와서 DB에 저장
    @Transactional
    public List<GenreDTO.Response> syncGenresFromTMDB() {
        List<GenreDTO.Response> syncedGenres = new ArrayList<>();
        
        // 영화 장르 동기화
        TMDBGenreResponse movieGenres = fetchMovieGenresFromTMDB();
        if (movieGenres != null && movieGenres.getGenres() != null) {
            for (TMDBGenre tmdbGenre : movieGenres.getGenres()) {
                GenreDTO.Response genre = saveOrUpdateGenre(tmdbGenre);
                syncedGenres.add(genre);
            }
        }
        
        // TV 장르 동기화 (중복 제거)
        TMDBGenreResponse tvGenres = fetchTVGenresFromTMDB();
        if (tvGenres != null && tvGenres.getGenres() != null) {
            for (TMDBGenre tmdbGenre : tvGenres.getGenres()) {
                Optional<Genre> existingGenre = genreRepository.findById(tmdbGenre.getId().longValue());
                if (existingGenre.isEmpty()) {
                    GenreDTO.Response genre = saveOrUpdateGenre(tmdbGenre);
                    syncedGenres.add(genre);
                }
            }
        }
        
        return syncedGenres;
    }

    // 장르 저장 또는 업데이트
    private GenreDTO.Response saveOrUpdateGenre(TMDBGenre tmdbGenre) {
        Optional<Genre> existingGenre = genreRepository.findById(tmdbGenre.getId().longValue());
        
        Genre genre;
        if (existingGenre.isPresent()) {
            // 기존 장르 업데이트
            genre = existingGenre.get();
            genre.setName(tmdbGenre.getName());
        } else {
            // 새 장르 생성
            genre = Genre.builder()
                    .id(tmdbGenre.getId().longValue())
                    .name(tmdbGenre.getName())
                    .build();
        }
        
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toResponse(savedGenre);
    }
}
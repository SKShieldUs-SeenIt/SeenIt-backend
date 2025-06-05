package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.MovieDTO;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.mapper.MovieMapper;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieMapper movieMapper;
    private final ContentSearchService contentSearchService;

    // 영화 목록 조회 (페이징, 정렬)
    public Page<MovieDTO.ListResponse> getMovies(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Movie> moviePage = movieRepository.findAll(pageable);
        return moviePage.map(movieMapper::toListResponse);
    }

    // 영화 상세 조회
    public MovieDTO.Response getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        return movieMapper.toResponse(movie);
    }

    // TMDB ID로 영화 조회
    public MovieDTO.Response getMovieByTmdbId(Long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        return movieMapper.toResponse(movie);
    }

    // 영화 생성 (관리자용)
    @Transactional
    public MovieDTO.Response createMovie(MovieDTO.CreateRequest request) {
        // 중복 확인
        if (movieRepository.existsByTmdbId(request.getTmdbId())) {
            throw new BusinessException(ErrorCode.MOVIE_ALREADY_EXISTS);
        }

        Movie movie = movieMapper.toEntity(request);

        // 장르 설정
        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            List<Genre> genres = genreRepository.findAllById(request.getGenreIds());
            if (genres.size() != request.getGenreIds().size()) {
                throw new BusinessException(ErrorCode.GENRE_NOT_FOUND);
            }
            movie.setGenres(genres);
        }

        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toResponse(savedMovie);
    }

    // 영화 수정 (관리자용)
    @Transactional
    public MovieDTO.Response updateMovie(Long id, MovieDTO.UpdateRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        updateMovieFields(movie, request);

        // 장르 업데이트
        if (request.getGenreIds() != null) {
            List<Genre> genres = genreRepository.findAllById(request.getGenreIds());
            if (genres.size() != request.getGenreIds().size()) {
                throw new BusinessException(ErrorCode.GENRE_NOT_FOUND);
            }
            movie.setGenres(genres);
        }

        Movie updatedMovie = movieRepository.save(movie);
        return movieMapper.toResponse(updatedMovie);
    }

    // 영화 삭제 (관리자용)
    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
        }
        movieRepository.deleteById(id);
    }

    // 영화 검색
    public Page<MovieDTO.ListResponse> searchMovies(MovieDTO.SearchRequest searchRequest, int page, int size) {
        // 로컬 DB 검색
        Page<Movie> localResults = performLocalSearch(searchRequest, page, size);

        // API 검색이 필요한 경우 (결과가 적거나 제목 검색인 경우)
        if (localResults.getTotalElements() < 10 || searchRequest.getTitle() != null) {
            // TMDB API 검색 및 저장 (ContentSearchService 활용)
            if (contentSearchService != null) {
                contentSearchService.searchAndSaveMovies(searchRequest.getTitle(), page);
                // 다시 로컬 검색
                localResults = performLocalSearch(searchRequest, page, size);
            }
        }

        return localResults.map(movieMapper::toListResponse);
    }

    // 장르별 영화 조회
    public Page<MovieDTO.ListResponse> getMoviesByGenre(Long genreId, int page, int size) {
        if (!genreRepository.existsById(genreId)) {
            throw new BusinessException(ErrorCode.GENRE_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("voteAverage").descending());
        Page<Movie> moviePage = movieRepository.findByGenreId(genreId, pageable);

        return moviePage.map(movieMapper::toListResponse);
    }

    // 인기 영화 조회
    public List<MovieDTO.ListResponse> getPopularMovies(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("voteAverage").descending());
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        return moviePage.getContent().stream()
                .map(movieMapper::toListResponse)
                .toList();
    }

    // 평점 높은 영화 조회
    public List<MovieDTO.ListResponse> getTopRatedMovies() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("combinedRating").descending());
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        return moviePage.getContent().stream()
                .map(movieMapper::toListResponse)
                .toList();
    }

    // 개봉년도별 영화 조회
    public List<MovieDTO.ListResponse> getMoviesByReleaseYear(String year) {
        List<Movie> movies = movieRepository.findByReleaseYear(year);
        return movies.stream()
                .map(movieMapper::toListResponse)
                .toList();
    }

    // private 메서드들
    private void updateMovieFields(Movie movie, MovieDTO.UpdateRequest request) {
        if (request.getTitle() != null) {
            movie.setTitle(request.getTitle());
        }
        if (request.getOverview() != null) {
            movie.setOverview(request.getOverview());
        }
        if (request.getReleaseDate() != null) {
            movie.setReleaseDate(request.getReleaseDate());
        }
        if (request.getPosterPath() != null) {
            movie.setPosterPath(request.getPosterPath());
        }
        if (request.getVoteAverage() != null) {
            movie.setVoteAverage(request.getVoteAverage());
        }
        if (request.getVoteCount() != null) {
            movie.setVoteCount(request.getVoteCount());
        }
    }

    private Page<Movie> performLocalSearch(MovieDTO.SearchRequest searchRequest, int page, int size) {
        String validatedSortBy = validateAndConvertSortBy(searchRequest.getSortBy());
        // 정렬 설정
        Sort sort = Sort.by(
                Sort.Direction.fromString(searchRequest.getSortDirection()),
                validatedSortBy
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        // 검색 조건에 따른 쿼리 실행
        if (searchRequest.getTitle() != null && !searchRequest.getTitle().trim().isEmpty()) {
            return movieRepository.findByTitleContainingIgnoreCase(searchRequest.getTitle(), pageable);
        } else if (searchRequest.getGenreIds() != null && !searchRequest.getGenreIds().isEmpty()) {
            return movieRepository.findByGenres_IdIn(searchRequest.getGenreIds(), pageable);
        } else if (searchRequest.getMinRating() != null || searchRequest.getMaxRating() != null) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 10.0;
            return movieRepository.findByCombinedRatingBetween(minRating, maxRating, pageable);
        } else {
            return movieRepository.findAll(pageable);
        }
    }

    private String validateAndConvertSortBy(String sortBy) {
        if (sortBy == null) {
            return "combinedRating"; // 통합 평점을 기본값으로
        }

        switch (sortBy.toLowerCase()) {
            case "rating":
                return "combinedRating";      // 통합 평점
            case "tmdbrating":
                return "voteAverage";         // TMDB 평점
            case "title":
                return "title";
            case "releasedate":
                return "releaseDate";
            case "votecount":
                return "voteCount";
            case "voteaverage":
                return "voteAverage";
            default:
                return "combinedRating";      // 기본값
        }
    }
}
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
    private final RatingService ratingService; // 🆕 추가

    // 🔥 수정된 영화 목록 조회
    public Page<MovieDTO.ListResponse> getMovies(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Movie> moviePage = movieRepository.findAll(pageable);
        return moviePage.map(movieMapper::toListResponse);
    }

    // 🔥 수정된 영화 상세 조회
    public MovieDTO.Response getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        // Repository 기반 평점 계산
        Double newRating = ratingService.calculateMovieCombinedRating(id);
        movie.setCombinedRating(newRating);
        movieRepository.save(movie);

        return movieMapper.toResponse(movie);
    }

    // 🔥 수정된 TMDB ID로 영화 조회
    public MovieDTO.Response getMovieByTmdbId(Long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        // Repository 기반 평점 계산
        Double newRating = ratingService.calculateMovieCombinedRating(movie.getId());
        movie.setCombinedRating(newRating);
        movieRepository.save(movie);

        return movieMapper.toResponse(movie);
    }

    // 🔥 수정된 영화 생성
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

        // 🔥 Repository 기반 평점 계산
        Double newRating = ratingService.calculateMovieCombinedRating(savedMovie.getId());
        savedMovie.setCombinedRating(newRating);
        movieRepository.save(savedMovie);

        return movieMapper.toResponse(savedMovie);
    }

    // 🔥 수정된 영화 수정
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

        // 🔥 Repository 기반 평점 계산
        Double newRating = ratingService.calculateMovieCombinedRating(id);
        movie.setCombinedRating(newRating);

        Movie updatedMovie = movieRepository.save(movie);
        return movieMapper.toResponse(updatedMovie);
    }

    // 🔥 수정된 영화 검색
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

        // 🔥 수정: 컬렉션 참조 대신 Repository 기반 계산
        if (!localResults.getContent().isEmpty()) {
            for (Movie movie : localResults.getContent()) {
                Double newRating = ratingService.calculateMovieCombinedRating(movie.getId());
                movie.setCombinedRating(newRating);
            }
            movieRepository.saveAll(localResults.getContent());
        }

        return localResults.map(movieMapper::toListResponse);
    }

    // 🔥 수정된 평점 수정 메서드 (완전 제거하거나 Repository 기반으로)
    @Transactional
    public void updateMovieCombinedRatings(List<Long> movieIds) {
        for (Long movieId : movieIds) {
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie != null) {
                Double newRating = ratingService.calculateMovieCombinedRating(movieId);
                movie.setCombinedRating(newRating);
                movieRepository.save(movie);
            }
        }
    }

    // 🔥 완전히 새로운 안전한 평점 수정 메서드
    @Transactional
    public void fixAllCombinedRatings() {
        List<Movie> allMovies = movieRepository.findAll();

        for (Movie movie : allMovies) {
            try {
                // 🔥 Repository 기반 계산 (컬렉션 참조 X)
                Double newRating = ratingService.calculateMovieCombinedRating(movie.getId());
                movie.setCombinedRating(newRating);

                System.out.println("영화 ID " + movie.getId() + " (" + movie.getTitle() + ") - " +
                        "combinedRating: " + movie.getCombinedRating());
            } catch (Exception e) {
                System.err.println("영화 ID " + movie.getId() + " 업데이트 실패: " + e.getMessage());
            }
        }

        movieRepository.saveAll(allMovies);
        System.out.println("모든 영화 combinedRating 업데이트 완료!");
    }

    // 영화 삭제 (관리자용)
    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
        }
        movieRepository.deleteById(id);
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
        Sort sort = Sort.by(
                Sort.Direction.fromString(searchRequest.getSortDirection()),
                validatedSortBy
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        // 🎯 3가지 핵심 조건만 사용한 복합 검색
        boolean hasTitle = searchRequest.getTitle() != null && !searchRequest.getTitle().trim().isEmpty();
        boolean hasGenres = searchRequest.getGenreIds() != null &&
                !searchRequest.getGenreIds().isEmpty() &&
                searchRequest.getGenreIds().stream().anyMatch(id -> id != null); // null 요소 체크 추가
        boolean hasRating = searchRequest.getMinRating() != null || searchRequest.getMaxRating() != null;

        // 1. 🏆 최고급 검색: 제목 + 장르 + 평점
        if (hasTitle && hasGenres && hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;

            return movieRepository.findByTitleContainingIgnoreCaseAndGenres_IdInAndCombinedRatingBetween(
                    searchRequest.getTitle(),
                    searchRequest.getGenreIds(),
                    minRating,
                    maxRating,
                    pageable
            );
        }

        // 2. 제목 + 장르
        else if (hasTitle && hasGenres) {
            return movieRepository.findByTitleContainingIgnoreCaseAndGenres_IdIn(
                    searchRequest.getTitle(),
                    searchRequest.getGenreIds(),
                    pageable
            );
        }

        // 3. 제목 + 평점
        else if (hasTitle && hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;

            return movieRepository.findByTitleContainingIgnoreCaseAndCombinedRatingBetween(
                    searchRequest.getTitle(),
                    minRating,
                    maxRating,
                    pageable
            );
        }

        // 4. 장르 + 평점
        else if (hasGenres && hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;

            return movieRepository.findByGenres_IdInAndCombinedRatingBetween(
                    searchRequest.getGenreIds(),
                    minRating,
                    maxRating,
                    pageable
            );
        }

        // 5. 단일 조건들
        else if (hasTitle) {
            return movieRepository.findByTitleContainingIgnoreCase(searchRequest.getTitle(), pageable);
        }
        else if (hasGenres) {
            return movieRepository.findByGenres_IdIn(searchRequest.getGenreIds(), pageable);
        }
        else if (hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;
            return movieRepository.findByCombinedRatingBetween(minRating, maxRating, pageable);
        }

        // 6. 조건 없으면 통합 평점 순으로 전체 조회
        else {
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
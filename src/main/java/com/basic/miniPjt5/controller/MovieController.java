package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.MovieDTO;
import com.basic.miniPjt5.DTO.PageResponseDTO;
import com.basic.miniPjt5.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // 영화 목록 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "voteAverage") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Page<MovieDTO.ListResponse> moviePage = movieService.getMovies(page, size, sortBy, sortDirection);

        PageResponseDTO<MovieDTO.ListResponse> response = PageResponseDTO.<MovieDTO.ListResponse>builder()
                .content(moviePage.getContent())
                .currentPage(moviePage.getNumber())
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .size(moviePage.getSize())
                .hasNext(moviePage.hasNext())
                .hasPrevious(moviePage.hasPrevious())
                .isFirst(moviePage.isFirst())
                .isLast(moviePage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    // 영화 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO.Response> getMovie(@PathVariable Long id) {
        MovieDTO.Response movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    // TMDB ID로 영화 조회
    @GetMapping("/tmdb/{tmdbId}")
    public ResponseEntity<MovieDTO.Response> getMovieByTmdbId(@PathVariable Long tmdbId) {
        MovieDTO.Response movie = movieService.getMovieByTmdbId(tmdbId);
        return ResponseEntity.ok(movie);
    }

    // 영화 생성 (관리자용)
    @PostMapping
    public ResponseEntity<MovieDTO.Response> createMovie(
            @Valid @RequestBody MovieDTO.CreateRequest request) {
        
        MovieDTO.Response createdMovie = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    // 영화 수정 (관리자용)
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO.Response> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieDTO.UpdateRequest request) {
        
        MovieDTO.Response updatedMovie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(updatedMovie);
    }

    // 영화 삭제 (관리자용)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    // 영화 검색
    @PostMapping("/search")
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> searchMovies(
            @Valid @RequestBody MovieDTO.SearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<MovieDTO.ListResponse> moviePage = movieService.searchMovies(searchRequest, page, size);

        PageResponseDTO<MovieDTO.ListResponse> response = PageResponseDTO.<MovieDTO.ListResponse>builder()
                .content(moviePage.getContent())
                .currentPage(moviePage.getNumber())
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .size(moviePage.getSize())
                .hasNext(moviePage.hasNext())
                .hasPrevious(moviePage.hasPrevious())
                .isFirst(moviePage.isFirst())
                .isLast(moviePage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    // GET 방식 영화 검색 (간단한 제목 검색)
    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> searchMoviesByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "popularity") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        MovieDTO.SearchRequest searchRequest = MovieDTO.SearchRequest.builder()
                .title(title)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<MovieDTO.ListResponse> moviePage = movieService.searchMovies(searchRequest, page, size);

        PageResponseDTO<MovieDTO.ListResponse> response = PageResponseDTO.<MovieDTO.ListResponse>builder()
                .content(moviePage.getContent())
                .currentPage(moviePage.getNumber())
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .size(moviePage.getSize())
                .hasNext(moviePage.hasNext())
                .hasPrevious(moviePage.hasPrevious())
                .isFirst(moviePage.isFirst())
                .isLast(moviePage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    // 장르별 영화 조회
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> getMoviesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<MovieDTO.ListResponse> moviePage = movieService.getMoviesByGenre(genreId, page, size);

        PageResponseDTO<MovieDTO.ListResponse> response = PageResponseDTO.<MovieDTO.ListResponse>builder()
                .content(moviePage.getContent())
                .currentPage(moviePage.getNumber())
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .size(moviePage.getSize())
                .hasNext(moviePage.hasNext())
                .hasPrevious(moviePage.hasPrevious())
                .isFirst(moviePage.isFirst())
                .isLast(moviePage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    // 인기 영화 조회
    @GetMapping("/popular")
    public ResponseEntity<List<MovieDTO.ListResponse>> getPopularMovies(
            @RequestParam(defaultValue = "20") int limit) {
        
        List<MovieDTO.ListResponse> popularMovies = movieService.getPopularMovies(limit);
        return ResponseEntity.ok(popularMovies);
    }
}
package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.MovieDTO;
import com.basic.miniPjt5.DTO.PageResponseDTO;
import com.basic.miniPjt5.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "영화", description = "영화 관련 API")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    @Operation(summary = "영화 목록 조회", description = "페이징과 정렬을 지원하는 영화 목록 조회")
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> getMovies(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "voteAverage")
            @RequestParam(defaultValue = "voteAverage") String sortBy,
            @Parameter(description = "정렬 방향", example = "desc")
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

    @GetMapping("/{id}")
    @Operation(summary = "영화 상세 조회", description = "ID로 특정 영화의 상세 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "영화를 찾을 수 없음")
    })
    public ResponseEntity<MovieDTO.Response> getMovie(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long id) {
        MovieDTO.Response movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/tmdb/{tmdbId}")
    @Operation(summary = "TMDB ID로 영화 조회", description = "TMDB ID로 영화 정보 조회")
    public ResponseEntity<MovieDTO.Response> getMovieByTmdbId(
            @Parameter(description = "TMDB ID", example = "550")
            @PathVariable Long tmdbId) {
        MovieDTO.Response movie = movieService.getMovieByTmdbId(tmdbId);
        return ResponseEntity.ok(movie);
    }

    @PostMapping
    @Operation(summary = "영화 생성", description = "새로운 영화 등록 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<MovieDTO.Response> createMovie(
            @Valid @RequestBody MovieDTO.CreateRequest request) {

        MovieDTO.Response createdMovie = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @PutMapping("/{id}")
    @Operation(summary = "영화 수정", description = "기존 영화 정보 수정 (관리자 전용)")
    public ResponseEntity<MovieDTO.Response> updateMovie(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MovieDTO.UpdateRequest request) {

        MovieDTO.Response updatedMovie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "영화 삭제", description = "영화 삭제 (관리자 전용)")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "영화 상세 검색", description = "다양한 조건으로 영화 검색")
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> searchMovies(
            @Valid @RequestBody MovieDTO.SearchRequest searchRequest,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
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

    @GetMapping("/search")
    @Operation(summary = "영화 제목 검색", description = "제목으로 간단한 영화 검색")
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> searchMoviesByTitle(
            @Parameter(description = "검색할 제목", example = "아바타")
            @RequestParam String title,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "popularity")
            @RequestParam(defaultValue = "popularity") String sortBy,
            @Parameter(description = "정렬 방향", example = "desc")
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

    @GetMapping("/genre/{genreId}")
    @Operation(summary = "장르별 영화 조회", description = "특정 장르의 영화 목록 조회")
    public ResponseEntity<PageResponseDTO<MovieDTO.ListResponse>> getMoviesByGenre(
            @Parameter(description = "장르 ID", example = "28")
            @PathVariable Long genreId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
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

    @GetMapping("/popular")
    @Operation(summary = "인기 영화 조회", description = "인기 영화 목록 조회")
    public ResponseEntity<List<MovieDTO.ListResponse>> getPopularMovies(
            @Parameter(description = "조회할 개수", example = "20")
            @RequestParam(defaultValue = "20") int limit) {

        List<MovieDTO.ListResponse> popularMovies = movieService.getPopularMovies(limit);
        return ResponseEntity.ok(popularMovies);
    }
}
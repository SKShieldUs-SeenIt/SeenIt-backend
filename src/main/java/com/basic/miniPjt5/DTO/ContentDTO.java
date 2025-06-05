package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

public class ContentDTO {

    @Schema(description = "통합 검색 결과 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResult {

        @Schema(description = "검색된 영화 목록")
        private List<MovieDTO.ListResponse> movies;

        @Schema(description = "검색된 드라마 목록")
        private List<DramaDTO.ListResponse> dramas;

        @Schema(description = "검색 키워드", example = "아바타")
        private String query;

        @Schema(description = "영화 검색 결과 수", example = "15")
        private Integer totalMovieResults;

        @Schema(description = "드라마 검색 결과 수", example = "8")
        private Integer totalDramaResults;

        @Schema(description = "전체 검색 결과 수", example = "23")
        private Integer totalResults;

        @Schema(description = "현재 페이지", example = "0")
        private Integer currentPage;

        @Schema(description = "전체 페이지 수", example = "3")
        private Integer totalPages;

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        private Boolean hasNext;

        @Schema(description = "이전 페이지 존재 여부", example = "false")
        private Boolean hasPrevious;
    }

    @Schema(description = "통합 검색 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {

        @Schema(description = "검색 키워드", example = "아바타", required = true)
        @NotBlank(message = "검색어는 필수입니다.")
        @Size(min = 2, max = 100, message = "검색어는 2자 이상 100자 이하여야 합니다.")
        private String query;

        @Schema(description = "장르 ID 목록", example = "[28, 12, 878]")
        private List<Long> genreIds;

        @Schema(description = "콘텐츠 타입", example = "ALL", allowableValues = {"MOVIE", "DRAMA", "ALL"})
        private String contentType;

        @Schema(description = "최소 평점", example = "7.0", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "최소 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최소 평점은 10.0 이하여야 합니다.")
        private Double minRating;

        @Schema(description = "정렬 기준", example = "popularity", allowableValues = {"popularity", "rating", "release_date", "title"})
        @Pattern(regexp = "popularity|rating|release_date|title",
                message = "정렬 기준은 popularity, rating, release_date, title 중 하나여야 합니다.")
        private String sortBy = "popularity";

        @Schema(description = "정렬 방향", example = "desc", allowableValues = {"asc", "desc"})
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc여야 합니다.")
        private String sortDirection = "desc";

        @Schema(description = "TMDB API 사용 여부", example = "false")
        private Boolean useApi = false;
    }
}
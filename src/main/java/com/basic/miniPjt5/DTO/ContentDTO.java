// 통합 검색 결과 DTO
package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

public class ContentDTO {

    // 통합 검색 결과 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResult {
        
        private List<MovieDTO.ListResponse> movies;
        private List<DramaDTO.ListResponse> dramas;
        private String query;
        private Integer totalMovieResults;
        private Integer totalDramaResults;
        private Integer totalResults;
        private Integer currentPage;
        private Integer totalPages;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }

    // 통합 검색 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {
        
        @NotBlank(message = "검색어는 필수입니다.")
        @Size(min = 2, max = 100, message = "검색어는 2자 이상 100자 이하여야 합니다.")
        private String query;
        
        private List<Long> genreIds;
        private String contentType; // MOVIE, DRAMA, ALL
        
        @DecimalMin(value = "0.0", message = "최소 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최소 평점은 10.0 이하여야 합니다.")
        private Double minRating;
        
        @Pattern(regexp = "popularity|rating|release_date|title",
                message = "정렬 기준은 popularity, rating, release_date, title 중 하나여야 합니다.")
        private String sortBy = "popularity";
        
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc여야 합니다.")
        private String sortDirection = "desc";
        
        private Boolean useApi = false; // TMDB API 사용 여부
    }
}
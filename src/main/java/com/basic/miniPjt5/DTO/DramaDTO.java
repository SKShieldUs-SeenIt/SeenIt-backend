// DramaDTO - static 내부 클래스로 구현
package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

public class DramaDTO {

    // 드라마 생성 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        
        @NotNull(message = "TMDB ID는 필수입니다.")
        private Long tmdbId;
        
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다.")
        private String title;
        
        @Size(max = 2000, message = "줄거리는 2000자를 초과할 수 없습니다.")
        private String overview;
        
        private String firstAirDate;
        private String lastAirDate;
        private String posterPath;
        
        @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "평점은 10.0 이하여야 합니다.")
        private Double voteAverage;
        
        @Min(value = 0, message = "투표 수는 0 이상이어야 합니다.")
        private Integer voteCount;
        
        @Min(value = 1, message = "시즌 수는 1 이상이어야 합니다.")
        private Integer numberOfSeasons;
        
        @Min(value = 1, message = "에피소드 수는 1 이상이어야 합니다.")
        private Integer numberOfEpisodes;
        
        private List<Long> genreIds; // 장르 ID 목록
    }

    // 드라마 수정 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        
        @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다.")
        private String title;
        
        @Size(max = 2000, message = "줄거리는 2000자를 초과할 수 없습니다.")
        private String overview;
        
        private String firstAirDate;
        private String lastAirDate;
        private String posterPath;
        
        @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "평점은 10.0 이하여야 합니다.")
        private Double voteAverage;
        
        @Min(value = 0, message = "투표 수는 0 이상이어야 합니다.")
        private Integer voteCount;
        
        @Min(value = 1, message = "시즌 수는 1 이상이어야 합니다.")
        private Integer numberOfSeasons;
        
        @Min(value = 1, message = "에피소드 수는 1 이상이어야 합니다.")
        private Integer numberOfEpisodes;
        
        private List<Long> genreIds;
    }

    // 드라마 상세 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        
        private Long id;
        private Long tmdbId;
        private String title;
        private String overview;
        private String firstAirDate;
        private String lastAirDate;
        private String posterPath;
        private Double voteAverage;
        private Integer voteCount;
        private Integer numberOfSeasons;
        private Integer numberOfEpisodes;
        private List<GenreInfo> genres;
        private List<ReviewInfo> reviews;
        private Integer reviewCount;
        private Double userAverageRating; // 사용자들의 평균 평점
        private String createdAt;
        private String updatedAt;
    }

    // 드라마 목록용 응답 DTO (요약 정보)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        
        private Long id;
        private Long tmdbId;
        private String title;
        private String posterPath;
        private Double voteAverage;
        private Integer voteCount;
        private String firstAirDate;
        private Integer numberOfSeasons;
        private Integer numberOfEpisodes;
        private List<String> genreNames; // 장르명만 간단히
        private Integer reviewCount;
        private Double userAverageRating;
    }

    // 드라마 검색 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {
        
        private String title;
        private List<Long> genreIds;
        private String firstAirYear;
        
        @DecimalMin(value = "0.0", message = "최소 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최소 평점은 10.0 이하여야 합니다.")
        private Double minRating;
        
        @DecimalMin(value = "0.0", message = "최대 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최대 평점은 10.0 이하여야 합니다.")
        private Double maxRating;
        
        @Min(value = 1, message = "최소 시즌 수는 1 이상이어야 합니다.")
        private Integer minSeasons;
        
        @Min(value = 1, message = "최대 시즌 수는 1 이상이어야 합니다.")
        private Integer maxSeasons;
        
        @Pattern(regexp = "title|rating|first_air_date|seasons|episodes", 
                message = "정렬 기준은 title, rating, first_air_date, seasons, episodes 중 하나여야 합니다.")
        private String sortBy = "first_air_date";
        
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc여야 합니다.")
        private String sortDirection = "desc";
    }

    // 장르 정보 (내부 클래스)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GenreInfo {
        private Long id;
        private String name;
    }

    // 리뷰 정보 (내부 클래스)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewInfo {
        private Long id;
        private String content;
        private String username;
        private Long userId;
        private String createdAt;
        private String updatedAt;
    }
}
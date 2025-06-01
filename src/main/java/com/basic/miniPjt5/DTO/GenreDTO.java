// GenreDTO - static 내부 클래스로 구현
package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

public class GenreDTO {

    // 장르 생성 요청 DTO (TMDB API에서 가져온 데이터용)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotNull(message = "장르 ID는 필수입니다.")
        @Positive(message = "장르 ID는 양수여야 합니다.")
        private Long id; // TMDB 장르 ID를 그대로 사용

        @NotBlank(message = "장르명은 필수입니다.")
        @Size(min = 1, max = 100, message = "장르명은 1자 이상 100자 이하여야 합니다.")
        private String name;
    }

    // 장르 수정 요청 DTO (관리자용)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @NotBlank(message = "장르명은 필수입니다.")
        @Size(min = 1, max = 100, message = "장르명은 1자 이상 100자 이하여야 합니다.")
        private String name;
    }

    // 장르 상세 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private String name;
        private Integer movieCount; // 해당 장르의 영화 수
        private Integer dramaCount; // 해당 장르의 드라마 수
        private Integer totalCount; // 전체 컨텐츠 수
        private Double averageRating; // 해당 장르의 평균 평점
        private String createdAt;
        private String updatedAt;
    }

    // 장르 목록용 응답 DTO (간단한 정보)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        private Long id;
        private String name;
        private Integer totalCount; // 전체 컨텐츠 수
    }

    // 장르 통계 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Statistics {

        private Long id;
        private String name;
        private Integer movieCount;
        private Integer dramaCount;
        private Integer totalCount;
        private Double averageMovieRating;
        private Double averageDramaRating;
        private Double overallAverageRating;
        private Integer totalReviews;
    }

    // 장르별 인기 컨텐츠 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PopularContent {

        private Long genreId;
        private String genreName;
        private List<MovieSummary> topMovies;
        private List<DramaSummary> topDramas;
    }

    // 내부 클래스들
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MovieSummary {
        private Long id;
        private String title;
        private String posterPath;
        private Double voteAverage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DramaSummary {
        private Long id;
        private String title;
        private String posterPath;
        private Double voteAverage;
        private Integer numberOfSeasons;
    }
}
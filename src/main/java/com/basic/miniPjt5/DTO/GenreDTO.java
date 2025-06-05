package com.basic.miniPjt5.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class GenreDTO {

    @Schema(description = "장르 생성 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @Schema(description = "TMDB 장르 ID", example = "28", required = true)
        @NotNull(message = "장르 ID는 필수입니다.")
        @Positive(message = "장르 ID는 양수여야 합니다.")
        private Long id;

        @Schema(description = "장르명", example = "액션", required = true)
        @NotBlank(message = "장르명은 필수입니다.")
        @Size(min = 1, max = 100, message = "장르명은 1자 이상 100자 이하여야 합니다.")
        private String name;
    }

    @Schema(description = "장르 수정 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @Schema(description = "장르명", example = "액션", required = true)
        @NotBlank(message = "장르명은 필수입니다.")
        @Size(min = 1, max = 100, message = "장르명은 1자 이상 100자 이하여야 합니다.")
        private String name;
    }

    @Schema(description = "장르 상세 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        @Schema(description = "장르 ID", example = "28")
        private Long id;

        @Schema(description = "장르명", example = "액션")
        private String name;

        @Schema(description = "해당 장르의 영화 수", example = "125")
        private Integer movieCount;

        @Schema(description = "해당 장르의 드라마 수", example = "87")
        private Integer dramaCount;

        @Schema(description = "전체 콘텐츠 수", example = "212")
        private Integer totalCount;

        @Schema(description = "해당 장르의 평균 평점", example = "7.8")
        private Double averageRating;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "수정일시", example = "2024-06-04T16:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
    }

    @Schema(description = "장르 목록용 간단한 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        @Schema(description = "장르 ID", example = "28")
        private Long id;

        @Schema(description = "장르명", example = "액션")
        private String name;

        @Schema(description = "전체 콘텐츠 수", example = "212")
        private Integer totalCount;
    }

    @Schema(description = "장르 통계 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Statistics {

        @Schema(description = "장르 ID", example = "28")
        private Long id;

        @Schema(description = "장르명", example = "액션")
        private String name;

        @Schema(description = "영화 수", example = "125")
        private Integer movieCount;

        @Schema(description = "드라마 수", example = "87")
        private Integer dramaCount;

        @Schema(description = "전체 콘텐츠 수", example = "212")
        private Integer totalCount;

        @Schema(description = "영화 평균 평점", example = "7.9")
        private Double averageMovieRating;

        @Schema(description = "드라마 평균 평점", example = "7.7")
        private Double averageDramaRating;

        @Schema(description = "전체 평균 평점", example = "7.8")
        private Double overallAverageRating;

        @Schema(description = "총 리뷰 수", example = "567")
        private Integer totalReviews;
    }

    @Schema(description = "장르별 인기 콘텐츠 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PopularContent {

        @Schema(description = "장르 ID", example = "28")
        private Long genreId;

        @Schema(description = "장르명", example = "액션")
        private String genreName;

        @Schema(description = "인기 영화 목록")
        private List<MovieSummary> topMovies;

        @Schema(description = "인기 드라마 목록")
        private List<DramaSummary> topDramas;
    }

    @Schema(description = "영화 요약 정보")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MovieSummary {

        @Schema(description = "영화 ID", example = "1")
        private Long id;

        @Schema(description = "영화 제목", example = "아바타: 물의 길")
        private String title;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.5")
        private Double voteAverage;
    }

    @Schema(description = "드라마 요약 정보")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DramaSummary {

        @Schema(description = "드라마 ID", example = "1")
        private Long id;

        @Schema(description = "드라마 제목", example = "오징어 게임")
        private String title;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.9")
        private Double voteAverage;

        @Schema(description = "시즌 수", example = "1")
        private Integer numberOfSeasons;
    }
}
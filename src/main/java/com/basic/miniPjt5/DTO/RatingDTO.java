package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

public class RatingDTO {

    @Schema(description = "별점 등록/수정 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @Schema(description = "별점 (1-10점)", example = "8", minimum = "1", maximum = "10", required = true)
        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 10, message = "별점은 10점 이하여야 합니다.")
        private Integer score;

        @Schema(description = "영화 ID (영화 평점시 필수)", example = "1")
        private Long movieId;

        @Schema(description = "드라마 ID (드라마 평점시 필수)", example = "1")
        private Long dramaId;
    }

    @Schema(description = "별점 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        @Schema(description = "별점 ID", example = "1")
        private Long id;

        @Schema(description = "별점", example = "8")
        private Integer score;

        @Schema(description = "사용자명", example = "홍길동")
        private String username;

        @Schema(description = "사용자 ID", example = "123")
        private Long userId;

        @Schema(description = "영화 ID", example = "1")
        private Long movieId;

        @Schema(description = "영화 제목", example = "아바타: 물의 길")
        private String movieTitle;

        @Schema(description = "영화 포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String moviePosterPath;

        @Schema(description = "드라마 ID", example = "1")
        private Long dramaId;

        @Schema(description = "드라마 제목", example = "오징어 게임")
        private String dramaTitle;

        @Schema(description = "드라마 포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String dramaPosterPath;

        @Schema(description = "콘텐츠 타입", example = "MOVIE", allowableValues = {"MOVIE", "DRAMA"})
        private String contentType;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2024-06-04T16:00:00")
        private String updatedAt;
    }

    @Schema(description = "평균 별점 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AverageResponse {

        @Schema(description = "콘텐츠 ID", example = "1")
        private Long contentId;

        @Schema(description = "콘텐츠 타입", example = "MOVIE", allowableValues = {"MOVIE", "DRAMA"})
        private String contentType;

        @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
        private String contentTitle;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 별점", example = "8.7")
        private Double averageScore;

        @Schema(description = "별점 개수", example = "1234")
        private Long ratingCount;

        @Schema(description = "TMDB 평점", example = "8.5")
        private Double tmdbRating;
    }

    @Schema(description = "별점 통계 상세 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticsResponse {

        @Schema(description = "콘텐츠 ID", example = "1")
        private Long contentId;

        @Schema(description = "콘텐츠 타입", example = "MOVIE")
        private String contentType;

        @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
        private String contentTitle;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 별점", example = "8.7")
        private Double averageScore;

        @Schema(description = "총 별점 개수", example = "1234")
        private Long totalRatingCount;

        @Schema(description = "점수별 분포", example = "{\"1\": 5, \"2\": 10, \"3\": 20, ...}")
        private Map<Integer, Long> scoreDistribution;

        @Schema(description = "표준편차", example = "1.2")
        private Double standardDeviation;

        @Schema(description = "TMDB 평점", example = "8.5")
        private Double tmdbRating;

        @Schema(description = "TMDB 투표 수", example = "5678")
        private Integer tmdbVoteCount;

        @Schema(description = "최고 점수", example = "10")
        private Integer highestScore;

        @Schema(description = "최저 점수", example = "1")
        private Integer lowestScore;

        @Schema(description = "최근 평점 동향")
        private List<RecentRatingTrend> recentTrends;
    }

    @Schema(description = "간단한 별점 정보 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleRating {

        @Schema(description = "콘텐츠 ID", example = "1")
        private Long contentId;

        @Schema(description = "콘텐츠 타입", example = "MOVIE")
        private String contentType;

        @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
        private String contentTitle;

        @Schema(description = "평균 별점", example = "8.7")
        private Double averageScore;

        @Schema(description = "별점 개수", example = "1234")
        private Long ratingCount;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "TMDB 평점", example = "8.5")
        private Double tmdbRating;
    }

    @Schema(description = "최근 별점 동향")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentRatingTrend {

        @Schema(description = "날짜", example = "2024-06-04")
        private String date;

        @Schema(description = "해당 날짜 평균 별점", example = "8.7")
        private Double averageScore;

        @Schema(description = "해당 날짜 별점 개수", example = "15")
        private Long ratingCount;
    }

    @Schema(description = "사용자별 별점 요약 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRatingSummary {

        @Schema(description = "사용자 ID", example = "123")
        private Long userId;

        @Schema(description = "사용자명", example = "홍길동")
        private String username;

        @Schema(description = "총 별점 개수", example = "45")
        private Integer totalRatings;

        @Schema(description = "평균 별점", example = "7.8")
        private Double averageRating;

        @Schema(description = "영화 별점 개수", example = "30")
        private Integer movieRatings;

        @Schema(description = "드라마 별점 개수", example = "15")
        private Integer dramaRatings;

        @Schema(description = "점수별 분포", example = "{\"1\": 2, \"2\": 3, \"3\": 5, ...}")
        private Map<Integer, Long> scoreDistribution;
    }
}
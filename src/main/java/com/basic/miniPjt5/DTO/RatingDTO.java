package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

public class RatingDTO {

    // 별점 생성/수정 요청 DTO (기존 유지)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 10, message = "별점은 10점 이하여야 합니다.")
        private Integer score;

        // 영화 ID 또는 드라마 ID 중 하나만 입력
        private Long movieId;
        private Long dramaId;
    }

    // 별점 응답 DTO (기존 확장)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private Integer score;
        private String username;
        private Long userId;

        // 영화 정보 (있을 경우)
        private Long movieId;
        private String movieTitle;
        private String moviePosterPath;

        // 드라마 정보 (있을 경우)
        private Long dramaId;
        private String dramaTitle;
        private String dramaPosterPath;

        private String contentType;
        private String createdAt;
        private String updatedAt;
    }

    // 작품별 평균 별점 응답 DTO (기존 유지)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AverageResponse {

        private Long contentId;
        private String contentType; // "MOVIE" 또는 "DRAMA"
        private String contentTitle;
        private String posterPath;
        private Double averageScore;
        private Long ratingCount;
        private Double tmdbRating; // TMDB 평점과 비교용
    }

    // 평점 통계 상세 정보 DTO (기존 확장)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticsResponse {

        private Long contentId;
        private String contentType;
        private String contentTitle;
        private String posterPath;
        private Double averageScore;
        private Long totalRatingCount;
        private Map<Integer, Long> scoreDistribution; // 점수별 개수 (1-10점)
        private Double standardDeviation; // 표준편차
        private Double tmdbRating; // TMDB 평점
        private Integer tmdbVoteCount; // TMDB 투표 수

        // 최고/최저 평점
        private Integer highestScore;
        private Integer lowestScore;

        // 최근 평점 동향
        private List<RecentRatingTrend> recentTrends;
    }

    // 간단한 평점 정보 DTO (기존 확장)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleRating {

        private Long contentId;
        private String contentType;
        private String contentTitle;
        private Double averageScore;
        private Long ratingCount;
        private String posterPath;
        private Double tmdbRating;
    }

    // 최근 평점 동향 (내부 클래스)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentRatingTrend {
        private String date; // YYYY-MM-DD
        private Double averageScore;
        private Long ratingCount;
    }

    // 사용자별 평점 요약 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRatingSummary {
        private Long userId;
        private String username;
        private Integer totalRatings;
        private Double averageRating;
        private Integer movieRatings;
        private Integer dramaRatings;
        private Map<Integer, Long> scoreDistribution;
    }
}
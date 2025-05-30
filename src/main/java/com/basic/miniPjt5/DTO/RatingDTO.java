package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

public class RatingDTO {

    // 별점 생성/수정 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 10, message = "별점은 10점 이하여야 합니다.")
        private Integer score;

        // 영화 ID 또는 드라마 ID 중 하나만 입력
        private Long movieId;
        private Long dramaId;
    }

    // 별점 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private Integer score;
        private String username;
        private Long userId;

        // 영화 정보 (있을 경우)
        private Long movieId;
        private String movieTitle;

        // 드라마 정보 (있을 경우)
        private Long dramaId;
        private String dramaTitle;

        private String createdAt;
        private String updatedAt;
    }

    // 작품별 평균 별점 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AverageResponse {

        private Long contentId;
        private String contentType; // "MOVIE" 또는 "DRAMA"
        private String contentTitle;
        private Double averageScore;
        private Long ratingCount;
    }

    // 평점 통계 상세 정보 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsResponse {

        private Long contentId;
        private String contentType;
        private String contentTitle;
        private Double averageScore;
        private Long totalRatingCount;
        private Map<Integer, Long> scoreDistribution; // 점수별 개수
        private Double standardDeviation; // 표준편차 (선택사항)
    }

    // 간단한 평점 정보 DTO (목록 조회용)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleRating {

        private Long contentId;
        private String contentType;
        private String contentTitle;
        private Double averageScore;
        private Long ratingCount;
        private String posterPath; // 포스터 이미지 경로
    }
}
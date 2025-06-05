package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.UserWatched;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class UserWatchedDTO {

    @Schema(description = "시청 기록 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        @Schema(description = "시청 기록 ID", example = "1")
        private Long id;

        @Schema(description = "콘텐츠 타입", example = "MOVIE")
        private UserWatched.ContentType contentType;

        @Schema(description = "콘텐츠 ID", example = "1")
        private Long contentId;

        @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
        private String contentTitle;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "개봉일/방영일", example = "2022-12-14")
        private String releaseDate;

        @Schema(description = "장르명 목록", example = "[\"액션\", \"모험\", \"SF\"]")
        private List<String> genreNames;

        @Schema(description = "TMDB 평점", example = "8.5")
        private Double voteAverage;

        @Schema(description = "통합 평점", example = "8.7")
        private Double combinedRating;

        @Schema(description = "시청한 날짜", example = "2024-06-04T20:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime watchedAt;

        // 드라마 전용 필드들 (contentType이 DRAMA일 때만 사용)
        @Schema(description = "시즌 수 (드라마인 경우)", example = "1")
        private Integer numberOfSeasons;

        @Schema(description = "에피소드 수 (드라마인 경우)", example = "9")
        private Integer numberOfEpisodes;
    }

    @Schema(description = "시청 기록 목록 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        @Schema(description = "시청 기록 ID", example = "1")
        private Long id;

        @Schema(description = "콘텐츠 타입", example = "MOVIE")
        private UserWatched.ContentType contentType;

        @Schema(description = "콘텐츠 ID", example = "1")
        private Long contentId;

        @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
        private String contentTitle;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "TMDB 평점", example = "8.5")
        private Double voteAverage;

        @Schema(description = "통합 평점", example = "8.7")
        private Double combinedRating;

        @Schema(description = "시청한 날짜", example = "2024-06-04T20:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime watchedAt;

        @Schema(description = "장르명 (첫 번째)", example = "액션")
        private String primaryGenre; // 주요 장르 하나만 표시
    }

    @Schema(description = "시청 통계 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatsResponse {

        @Schema(description = "총 시청한 영화 수", example = "25")
        private Long totalMovies;

        @Schema(description = "총 시청한 드라마 수", example = "15")
        private Long totalDramas;

        @Schema(description = "총 시청한 콘텐츠 수", example = "40")
        private Long totalContents;

        @Schema(description = "최근 시청한 콘텐츠", example = "아바타: 물의 길")
        private String recentlyWatched;

        @Schema(description = "이번 달 시청한 콘텐츠 수", example = "5")
        private Long thisMonthWatched;

        @Schema(description = "가장 많이 본 장르", example = "액션")
        private String favoriteGenre;

        @Schema(description = "평균 시청 콘텐츠 평점", example = "8.2")
        private Double averageContentRating;
    }
}
package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

public class DramaDTO {

    @Schema(description = "드라마 생성 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @Schema(description = "TMDB ID", example = "94605", required = true)
        @NotNull(message = "TMDB ID는 필수입니다.")
        private Long tmdbId;

        @Schema(description = "드라마 제목", example = "오징어 게임", required = true)
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다.")
        private String title;

        @Schema(description = "드라마 줄거리", example = "456명의 참가자들이...")
        @Size(max = 2000, message = "줄거리는 2000자를 초과할 수 없습니다.")
        private String overview;

        @Schema(description = "첫 방영일", example = "2021-09-17")
        private String firstAirDate;

        @Schema(description = "마지막 방영일", example = "2021-09-17")
        private String lastAirDate;

        @Schema(description = "포스터 경로", example = "/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.9", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "평점은 10.0 이하여야 합니다.")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "2567", minimum = "0")
        @Min(value = 0, message = "투표 수는 0 이상이어야 합니다.")
        private Integer voteCount;

        @Schema(description = "시즌 수", example = "1", minimum = "1")
        @Min(value = 1, message = "시즌 수는 1 이상이어야 합니다.")
        private Integer numberOfSeasons;

        @Schema(description = "에피소드 수", example = "9", minimum = "1")
        @Min(value = 1, message = "에피소드 수는 1 이상이어야 합니다.")
        private Integer numberOfEpisodes;

        @Schema(description = "장르 ID 목록", example = "[18, 9648]")
        private List<Long> genreIds;
    }

    @Schema(description = "드라마 수정 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @Schema(description = "드라마 제목", example = "오징어 게임")
        @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다.")
        private String title;

        @Schema(description = "드라마 줄거리", example = "456명의 참가자들이...")
        @Size(max = 2000, message = "줄거리는 2000자를 초과할 수 없습니다.")
        private String overview;

        @Schema(description = "첫 방영일", example = "2021-09-17")
        private String firstAirDate;

        @Schema(description = "마지막 방영일", example = "2021-09-17")
        private String lastAirDate;

        @Schema(description = "포스터 경로", example = "/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.9", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "평점은 10.0 이하여야 합니다.")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "2567", minimum = "0")
        @Min(value = 0, message = "투표 수는 0 이상이어야 합니다.")
        private Integer voteCount;

        @Schema(description = "시즌 수", example = "1", minimum = "1")
        @Min(value = 1, message = "시즌 수는 1 이상이어야 합니다.")
        private Integer numberOfSeasons;

        @Schema(description = "에피소드 수", example = "9", minimum = "1")
        @Min(value = 1, message = "에피소드 수는 1 이상이어야 합니다.")
        private Integer numberOfEpisodes;

        @Schema(description = "장르 ID 목록", example = "[18, 9648]")
        private List<Long> genreIds;
    }

    @Schema(description = "드라마 상세 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        @Schema(description = "드라마 ID", example = "1")
        private Long id;

        @Schema(description = "TMDB ID", example = "94605")
        private Long tmdbId;

        @Schema(description = "드라마 제목", example = "오징어 게임")
        private String title;

        @Schema(description = "드라마 줄거리", example = "456명의 참가자들이...")
        private String overview;

        @Schema(description = "첫 방영일", example = "2021-09-17")
        private String firstAirDate;

        @Schema(description = "마지막 방영일", example = "2021-09-17")
        private String lastAirDate;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.9")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "2567")
        private Integer voteCount;

        @Schema(description = "시즌 수", example = "1")
        private Integer numberOfSeasons;

        @Schema(description = "에피소드 수", example = "9")
        private Integer numberOfEpisodes;

        @Schema(description = "장르 목록")
        private List<GenreInfo> genres;

        @Schema(description = "리뷰 목록")
        private List<ReviewInfo> reviews;

        @Schema(description = "리뷰 개수", example = "45")
        private Integer reviewCount;

        @Schema(description = "사용자 평균 평점", example = "9.1")
        private Double userAverageRating;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2024-06-04T16:00:00")
        private String updatedAt;
    }

    @Schema(description = "드라마 목록 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        @Schema(description = "드라마 ID", example = "1")
        private Long id;

        @Schema(description = "TMDB ID", example = "94605")
        private Long tmdbId;

        @Schema(description = "드라마 제목", example = "오징어 게임")
        private String title;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.9")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "2567")
        private Integer voteCount;

        @Schema(description = "첫 방영일", example = "2021-09-17")
        private String firstAirDate;

        @Schema(description = "시즌 수", example = "1")
        private Integer numberOfSeasons;

        @Schema(description = "에피소드 수", example = "9")
        private Integer numberOfEpisodes;

        @Schema(description = "장르명 목록", example = "[\"드라마\", \"미스터리\"]")
        private List<String> genreNames;

        @Schema(description = "리뷰 개수", example = "45")
        private Integer reviewCount;

        @Schema(description = "사용자 평균 평점", example = "9.1")
        private Double userAverageRating;
    }

    @Schema(description = "드라마 검색 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {

        @Schema(description = "검색할 제목", example = "오징어")
        private String title;

        @Schema(description = "장르 ID 목록", example = "[18, 9648]")
        private List<Long> genreIds;

        @Schema(description = "첫 방영년도", example = "2021")
        private String firstAirYear;

        @Schema(description = "최소 평점", example = "8.0", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "최소 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최소 평점은 10.0 이하여야 합니다.")
        private Double minRating;

        @Schema(description = "최대 평점", example = "10.0", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "최대 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최대 평점은 10.0 이하여야 합니다.")
        private Double maxRating;

        @Schema(description = "최소 시즌 수", example = "1", minimum = "1")
        @Min(value = 1, message = "최소 시즌 수는 1 이상이어야 합니다.")
        private Integer minSeasons;

        @Schema(description = "최대 시즌 수", example = "5", minimum = "1")
        @Min(value = 1, message = "최대 시즌 수는 1 이상이어야 합니다.")
        private Integer maxSeasons;

        @Schema(description = "정렬 기준", example = "first_air_date", allowableValues = {"title", "rating", "first_air_date", "seasons", "episodes"})
        @Pattern(regexp = "title|rating|first_air_date|seasons|episodes",
                message = "정렬 기준은 title, rating, first_air_date, seasons, episodes 중 하나여야 합니다.")
        private String sortBy = "first_air_date";

        @Schema(description = "정렬 방향", example = "desc", allowableValues = {"asc", "desc"})
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc여야 합니다.")
        private String sortDirection = "desc";
    }

    @Schema(description = "장르 정보")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GenreInfo {

        @Schema(description = "장르 ID", example = "18")
        private Long id;

        @Schema(description = "장르명", example = "드라마")
        private String name;
    }

    @Schema(description = "리뷰 정보")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewInfo {

        @Schema(description = "리뷰 ID", example = "1")
        private Long id;

        @Schema(description = "리뷰 내용", example = "정말 충격적인 드라마였어요!")
        private String content;

        @Schema(description = "사용자명", example = "홍길동")
        private String username;

        @Schema(description = "사용자 ID", example = "123")
        private Long userId;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2024-06-04T16:00:00")
        private String updatedAt;
    }
}
package com.basic.miniPjt5.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dramas")
public class Drama implements Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drama_id") // 컬럼명 명시 (선택 사항)
    private Long id; // 내부 ID

    @Column(unique = true, nullable = false)
    private Long tmdbId; // TMDB에서 제공하는 고유 ID

    @Column(nullable = false)
    private String title; // 드라마 제목

    @ManyToMany // Drama와 Genre는 다대다 관계
    @JoinTable(
        name = "drama_genre", // 조인 테이블 이름
        joinColumns = @JoinColumn(name = "drama_id"), // Drama 엔티티의 PK
        inverseJoinColumns = @JoinColumn(name = "genre_id") // Genre 엔티티의 PK
    )
    @JsonIgnore
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    @Column(nullable = false)
    private Integer voteCount; // 투표 수

    @Column(nullable = false)
    private Double voteAverage; // 평균 별점

    @OneToMany(mappedBy = "drama", cascade = CascadeType.ALL, orphanRemoval = true) // Drama와 Review는 일대다 관계
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    // TMDB API에서 가져올 수 있는 추가 필드들 (선택 사항)
    @Column(length = 2000)
    private String overview;
    private String posterPath;
    private String firstAirDate; // 첫 방송일
    private String lastAirDate; // 마지막 방송일
    private Integer numberOfSeasons; // 시즌 수
    private Integer numberOfEpisodes; // 에피소드 수

    @OneToMany(mappedBy = "drama", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Rating> ratings = new ArrayList<>();

    // 통합 평균 평점 (TMDB + 사용자)
    @Column
    private Double combinedRating;

    // 통합 평점 계산 (Movie와 동일)
    public Double calculateCombinedRating() {
        double tmdbTotalScore = this.voteAverage/2 * this.voteCount;
        double userTotalScore = ratings.stream()
                .mapToDouble(rating -> rating.getScore().doubleValue())
                .sum();

        int totalVotes = this.voteCount + ratings.size();
        if (totalVotes == 0) return 0.0;

        double combinedAverage = (tmdbTotalScore + userTotalScore) / totalVotes;
        return Math.round(combinedAverage * 100.0) / 100.0;
    }

    public Double getUserAverageRating() {
        if (ratings.isEmpty()) return null;
        return ratings.stream()
                .mapToDouble(rating -> rating.getScore().doubleValue())
                .average()
                .orElse(0.0);
    }

    public void updateCombinedRating() {
        this.combinedRating = calculateCombinedRating();
    }
}
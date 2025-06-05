package com.basic.miniPjt5.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ratings",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_drama_id", columnList = "drama_id"),
                @Index(name = "idx_movie_id", columnList = "movie_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_movie", columnNames = {"user_id", "movie_id"}),
                @UniqueConstraint(name = "uk_user_drama", columnNames = {"user_id", "drama_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Rating extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    // 별점 점수 (1~10)
    @Column(nullable = false)
    @Min(1)
    @Max(10)
    private int score;

    // 작성자 (User FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 영화와의 관계 (선택적)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // 드라마와의 관계 (선택적)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drama_id")
    private Drama drama;

    // 별점 수정 메서드
    public void updateScore(int newScore) {
        if (newScore >= 1 && newScore <= 10) {
            this.score = newScore;
        } else {
            throw new IllegalArgumentException("별점은 1~10 사이여야 합니다.");
        }
    }

    // 생성자 - 영화 별점용
    public Rating(User user, int score, Movie movie) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("별점은 1~10 사이여야 합니다.");
        }
        this.user = user;
        this.score = score;
        this.movie = movie;
    }

    // 생성자 - 드라마 별점용
    public Rating(User user, int score, Drama drama) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("별점은 1~10 사이여야 합니다.");
        }
        this.user = user;
        this.score = score;
        this.drama = drama;
    }

    // 유틸리티 메서드
    public boolean isMovieRating() {
        return movie != null;
    }

    public boolean isDramaRating() {
        return drama != null;
    }

    // 데이터 검증 메서드
    @PrePersist
    @PreUpdate
    private void validateRating() {
        if ((movie == null && drama == null) || (movie != null && drama != null)) {
            throw new IllegalStateException("영화 또는 드라마 중 하나만 선택해야 합니다.");
        }
    }
}
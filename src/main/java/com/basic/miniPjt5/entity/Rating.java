package com.basic.miniPjt5.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ratings", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_drama_id", columnList = "drama_id"),
        @Index(name = "idx_movie_id", columnList = "movie_id")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Rating extends BaseEntity {

    // 별점 고유 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    // 별점 점수 (1~5)
    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int score;

    // 작성자 (User FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 드라마 고유 식별자
    @Column(name = "drama_id", nullable = true)
    private Long dramaId;

    // 영화 고유 식별자
    @Column(name = "movie_id", nullable = true)
    private Long movieId;

    // 별점 수정 메서드
    public void updateScore(int newScore) {
        if (newScore >= 1 && newScore <= 5) {
            this.score = newScore;
        } else {
            throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");
        }
    }

    // 생성자
    public Rating(User user, int score, Long dramaId, Long movieId) {
        this.user = user;
        this.score = score;
        this.dramaId = dramaId;
        this.movieId = movieId;
    }
}
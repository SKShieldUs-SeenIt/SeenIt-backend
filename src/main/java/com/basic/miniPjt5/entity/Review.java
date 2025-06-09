package com.basic.miniPjt5.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_movie_id", columnList = "movie_id"),
        @Index(name = "idx_drama_id", columnList = "drama_id"),
})
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = true, length = 2000)
    private String content;  // 리뷰 내용

    @Column(name = "likes_count")
    @Builder.Default
    private Integer likesCount = 0;  // 좋아요 수

    @Column(name = "is_spoiler")
    @Builder.Default
    private Boolean isSpoiler = false;  // 스포일러 여부

    // 영화와의 관계 (선택적 - 영화 또는 드라마 중 하나)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // 드라마와의 관계 (선택적 - 영화 또는 드라마 중 하나)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drama_id")
    private Drama drama;

    // 사용자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    // 좋아요 증가/감소 메서드
    public void increaseLikes() {
        this.likesCount++;
    }

    public void decreaseLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    // 스포일러 토글
    public void toggleSpoiler() {
        this.isSpoiler = !this.isSpoiler;
    }

    // 유틸리티 메서드
    public boolean isMovieReview() {
        return movie != null;
    }

    public boolean isDramaReview() {
        return drama != null;
    }

    // 데이터 검증 메서드
    @PrePersist
    @PreUpdate
    private void validateContent() {
        if ((movie == null && drama == null) || (movie != null && drama != null)) {
            throw new IllegalStateException("영화 또는 드라마 중 하나만 선택해야 합니다.");
        }
    }

    // 생성자 헬퍼 메서드
    public static Review createMovieReview(User user, Movie movie, String content, Boolean isSpoiler) {
        return Review.builder()
                .user(user)
                .movie(movie)
                .content(content)
                .isSpoiler(isSpoiler != null ? isSpoiler : false)
                .likesCount(0)
                .build();
    }

    public static Review createDramaReview(User user, Drama drama, String content, Boolean isSpoiler) {
        return Review.builder()
                .user(user)
                .drama(drama)
                .content(content)
                .isSpoiler(isSpoiler != null ? isSpoiler : false)
                .likesCount(0)
                .build();
    }

}
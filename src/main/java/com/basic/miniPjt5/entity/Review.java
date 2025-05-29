package com.basic.miniPjt5.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews")
public class Review extends BaseEntity {  // BaseEntity 상속 (created_at, updated_at 등)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false, length = 2000)
    private String content;  // 리뷰 내용

    @Column(nullable = false)
    private Double rating;   // 별점 (1.0 ~ 5.0)

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

    // 유틸리티 메서드
    public boolean isMovieReview() {
        return movie != null;
    }

    public boolean isDramaReview() {
        return drama != null;
    }
}
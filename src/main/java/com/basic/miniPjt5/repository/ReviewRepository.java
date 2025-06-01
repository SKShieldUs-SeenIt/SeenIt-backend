package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 영화별 리뷰 조회
    Page<Review> findByMovieIdOrderByCreatedAtDesc(Long movieId, Pageable pageable);
    List<Review> findByMovieId(Long movieId);

    // 드라마별 리뷰 조회
    Page<Review> findByDramaIdOrderByCreatedAtDesc(Long dramaId, Pageable pageable);
    List<Review> findByDramaId(Long dramaId);

    // 사용자별 리뷰 조회
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Review> findByUserId(Long userId);

    // 중복 리뷰 확인
    Optional<Review> findByUserIdAndMovieId(Long userId, Long movieId);
    Optional<Review> findByUserIdAndDramaId(Long userId, Long dramaId);

    // 최근 리뷰 조회
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findLatestReviews(Pageable pageable);

    // 컨텐츠별 리뷰 수 조회
    @Query("SELECT COUNT(r) FROM Review r WHERE r.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.drama.id = :dramaId")
    Long countByDramaId(@Param("dramaId") Long dramaId);

    // 특정 사용자가 작성한 리뷰인지 확인
    boolean existsByIdAndUserId(Long reviewId, Long userId);
}
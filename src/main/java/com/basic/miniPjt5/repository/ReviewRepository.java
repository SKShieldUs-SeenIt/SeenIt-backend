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
    
    // 특정 영화의 리뷰 목록 조회 (페이징)
    Page<Review> findByMovieIdOrderByCreatedAtDesc(Long movieId, Pageable pageable);
    
    // 특정 드라마의 리뷰 목록 조회 (페이징)
    Page<Review> findByDramaIdOrderByCreatedAtDesc(Long dramaId, Pageable pageable);
    
    // 특정 사용자의 리뷰 목록 조회 (페이징)
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 특정 사용자가 특정 영화에 작성한 리뷰 조회
    Optional<Review> findByUserIdAndMovieId(Long userId, Long movieId);
    
    // 특정 사용자가 특정 드라마에 작성한 리뷰 조회
    Optional<Review> findByUserIdAndDramaId(Long userId, Long dramaId);
    
    // 특정 영화의 리뷰 개수 조회
    long countByMovieId(Long movieId);
    
    // 특정 드라마의 리뷰 개수 조회
    long countByDramaId(Long dramaId);
    
    // 최신 리뷰 목록 조회 (전체)
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findLatestReviews(Pageable pageable);
    
    // 특정 사용자가 작성한 리뷰인지 확인
    boolean existsByIdAndUserId(Long reviewId, Long userId);
}
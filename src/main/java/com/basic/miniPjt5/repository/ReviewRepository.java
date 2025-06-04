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

    // 사용자별 리뷰 조회 - @Query 사용
    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId")
    List<Review> findByUserId(@Param("userId") Long userId);

    // 중복 리뷰 확인 - @Query 사용
    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId AND r.movie.id = :movieId")
    Optional<Review> findByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId AND r.drama.id = :dramaId")
    Optional<Review> findByUserIdAndDramaId(@Param("userId") Long userId, @Param("dramaId") Long dramaId);

    // 최근 리뷰 조회
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findLatestReviews(Pageable pageable);

    // 컨텐츠별 리뷰 수 조회
    @Query("SELECT COUNT(r) FROM Review r WHERE r.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.drama.id = :dramaId")
    Long countByDramaId(@Param("dramaId") Long dramaId);

    // 특정 사용자가 작성한 리뷰인지 확인
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.id = :reviewId AND r.user.userId = :userId")
    boolean existsByIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    // === 추가 유용한 쿼리들 ===

    // 사용자별 리뷰 수 조회
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // 좋아요가 많은 리뷰 조회 (영화)
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId ORDER BY r.likesCount DESC, r.createdAt DESC")
    Page<Review> findByMovieIdOrderByLikesDesc(@Param("movieId") Long movieId, Pageable pageable);

    // 좋아요가 많은 리뷰 조회 (드라마)
    @Query("SELECT r FROM Review r WHERE r.drama.id = :dramaId ORDER BY r.likesCount DESC, r.createdAt DESC")
    Page<Review> findByDramaIdOrderByLikesDesc(@Param("dramaId") Long dramaId, Pageable pageable);

    // 스포일러가 아닌 리뷰만 조회 (영화)
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId AND r.isSpoiler = false ORDER BY r.createdAt DESC")
    Page<Review> findNonSpoilerReviewsByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    // 스포일러가 아닌 리뷰만 조회 (드라마)
    @Query("SELECT r FROM Review r WHERE r.drama.id = :dramaId AND r.isSpoiler = false ORDER BY r.createdAt DESC")
    Page<Review> findNonSpoilerReviewsByDramaId(@Param("dramaId") Long dramaId, Pageable pageable);

    // 텍스트 검색 (제목 + 내용)
    @Query("SELECT r FROM Review r WHERE (LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY r.createdAt DESC")
    Page<Review> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 영화의 리뷰들
    List<Review> findByMovieId(Long movieId);

    // 특정 드라마의 리뷰들
    List<Review> findByDramaId(Long dramaId);

    // 특정 사용자의 리뷰들
    List<Review> findByUserId(Long userId);

    // 평점별 리뷰 조회
    List<Review> findByRatingGreaterThanEqual(Double rating);

    // 영화별 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") Long movieId);

    // 드라마별 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.drama.id = :dramaId")
    Double findAverageRatingByDramaId(@Param("dramaId") Long dramaId);

    // 영화별 리뷰 수
    @Query("SELECT COUNT(r) FROM Review r WHERE r.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    // 드라마별 리뷰 수
    @Query("SELECT COUNT(r) FROM Review r WHERE r.drama.id = :dramaId")
    Long countByDramaId(@Param("dramaId") Long dramaId);
}
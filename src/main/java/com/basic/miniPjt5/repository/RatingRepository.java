package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // 사용자별 특정 컨텐츠 별점 조회 - userId 필드명 명시
    @Query("SELECT r FROM Rating r WHERE r.user.userId = :userId AND r.movie.id = :movieId")
    Optional<Rating> findByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Query("SELECT r FROM Rating r WHERE r.user.userId = :userId AND r.drama.id = :dramaId")
    Optional<Rating> findByUserIdAndDramaId(@Param("userId") Long userId, @Param("dramaId") Long dramaId);

    @Query("SELECT r FROM Rating r WHERE r.user.userId = :userId")
    List<Rating> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Rating r WHERE r.user.userId = :userId ORDER BY r.createdAt DESC")
    Page<Rating> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    // 컨텐츠별 모든 별점 조회
    List<Rating> findByMovieId(Long movieId);
    List<Rating> findByDramaId(Long dramaId);
    Page<Rating> findByMovieIdOrderByCreatedAtDesc(Long movieId, Pageable pageable);
    Page<Rating> findByDramaIdOrderByCreatedAtDesc(Long dramaId, Pageable pageable);

    // 컨텐츠별 평균 별점 조회
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.movie.id = :movieId")
    Optional<Double> findAverageScoreByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.drama.id = :dramaId")
    Optional<Double> findAverageScoreByDramaId(@Param("dramaId") Long dramaId);

    // 컨텐츠별 별점 수 조회
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.movie.id = :movieId")
    Long countByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.drama.id = :dramaId")
    Long countByDramaId(@Param("dramaId") Long dramaId);

    // 점수 분포 조회
    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.movie.id = :movieId GROUP BY r.score")
    Object[][] findScoreDistributionByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.drama.id = :dramaId GROUP BY r.score")
    Object[][] findScoreDistributionByDramaId(@Param("dramaId") Long dramaId);

    // === 평점 통계 관련 추가 쿼리들 ===

    // 평점 높은 영화 목록 (평점 3개 이상인 작품만)
    @Query(value = """
        SELECT m.movie_id, 'MOVIE' as content_type, m.title, m.poster_path, 
               COALESCE(m.combined_rating, m.vote_average) as combined_score, 
               (m.vote_count + COALESCE(user_ratings.rating_count, 0)) as total_count
        FROM movies m 
        LEFT JOIN (
            SELECT r.movie_id, COUNT(r.rating_id) as rating_count 
            FROM ratings r 
            GROUP BY r.movie_id
        ) user_ratings ON m.movie_id = user_ratings.movie_id
        WHERE COALESCE(m.combined_rating, m.vote_average) IS NOT NULL
        ORDER BY combined_score DESC, total_count DESC
        """, nativeQuery = true)
    Page<Object[]> findTopRatedMovies(Pageable pageable);

    // 통합 평점 높은 드라마 목록 (combinedRating 기준)
    @Query(value = """
        SELECT d.drama_id, 'DRAMA' as content_type, d.title, d.poster_path, 
               COALESCE(d.combined_rating, d.vote_average) as combined_score,
               (d.vote_count + COALESCE(user_ratings.rating_count, 0)) as total_count
        FROM dramas d 
        LEFT JOIN (
            SELECT r.drama_id, COUNT(r.rating_id) as rating_count 
            FROM ratings r 
            GROUP BY r.drama_id
        ) user_ratings ON d.drama_id = user_ratings.drama_id
        WHERE COALESCE(d.combined_rating, d.vote_average) IS NOT NULL
        ORDER BY combined_score DESC, total_count DESC
        """, nativeQuery = true)
    Page<Object[]> findTopRatedDramas(Pageable pageable);

    // 최근 1주일간 평점이 많이 등록된 영화들 (통합 평점 포함)
    @Query(value = """
        SELECT m.movie_id, m.title, m.poster_path, 
               COALESCE(m.combined_rating, m.vote_average) as combined_score, 
               COUNT(r.rating_id) as recent_count
        FROM movies m 
        INNER JOIN ratings r ON m.movie_id = r.movie_id 
        WHERE r.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        GROUP BY m.movie_id, m.title, m.poster_path, m.combined_rating, m.vote_average
        ORDER BY recent_count DESC, combined_score DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findRecentlyPopularMovies(@Param("limit") int limit);

    // 최근 1주일간 평점이 많이 등록된 드라마들 (통합 평점 포함)
    @Query(value = """
        SELECT d.drama_id, d.title, d.poster_path, 
               COALESCE(d.combined_rating, d.vote_average) as combined_score, 
               COUNT(r.rating_id) as recent_count
        FROM dramas d 
        INNER JOIN ratings r ON d.drama_id = r.drama_id 
        WHERE r.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        GROUP BY d.drama_id, d.title, d.poster_path, d.combined_rating, d.vote_average
        ORDER BY recent_count DESC, combined_score DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findRecentlyPopularDramas(@Param("limit") int limit);

    // 영화 장르별 평균 평점 (통합 평점 기준)
    @Query(value = """
        SELECT g.name as genre_name, 
               AVG(COALESCE(m.combined_rating, m.vote_average)) as avg_combined_score, 
               COUNT(DISTINCT m.movie_id) as movie_count
        FROM genres g 
        INNER JOIN movie_genre mg ON g.genre_id = mg.genre_id 
        INNER JOIN movies m ON mg.movie_id = m.movie_id 
        WHERE COALESCE(m.combined_rating, m.vote_average) IS NOT NULL
        GROUP BY g.genre_id, g.name 
        HAVING COUNT(DISTINCT m.movie_id) >= 3
        ORDER BY avg_combined_score DESC
        """, nativeQuery = true)
    List<Object[]> findAverageRatingByMovieGenre();

    // 드라마 장르별 평균 평점 (통합 평점 기준)
    @Query(value = """
        SELECT g.name as genre_name, 
               AVG(COALESCE(d.combined_rating, d.vote_average)) as avg_combined_score, 
               COUNT(DISTINCT d.drama_id) as drama_count
        FROM genres g 
        INNER JOIN drama_genre dg ON g.genre_id = dg.genre_id 
        INNER JOIN dramas d ON dg.drama_id = d.drama_id 
        WHERE COALESCE(d.combined_rating, d.vote_average) IS NOT NULL
        GROUP BY g.genre_id, g.name 
        HAVING COUNT(DISTINCT d.drama_id) >= 3
        ORDER BY avg_combined_score DESC
        """, nativeQuery = true)
    List<Object[]> findAverageRatingByDramaGenre();

    // 사용자별 평균 평점 - userId 필드명으로 수정
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.user.userId = :userId")
    Optional<Double> findAverageScoreByUserId(@Param("userId") Long userId);

    // 사용자 평점 개수 - @Query로 명시적 작성
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // 사용자 평점 분포 - userId 필드명으로 수정
    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.user.userId = :userId GROUP BY r.score ORDER BY r.score")
    Object[][] findScoreDistributionByUserId(@Param("userId") Long userId);

    // 사용자가 10점을 준 작품들
    @Query(value = """
        SELECT 'MOVIE' as content_type, m.title, r.score, r.created_at
        FROM ratings r 
        INNER JOIN movies m ON r.movie_id = m.movie_id 
        WHERE r.user_id = :userId AND r.score = 10
        UNION ALL
        SELECT 'DRAMA' as content_type, d.title, r.score, r.created_at
        FROM ratings r 
        INNER JOIN dramas d ON r.drama_id = d.drama_id 
        WHERE r.user_id = :userId AND r.score = 10
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Object[]> findUserTopRatedContents(@Param("userId") Long userId);
}
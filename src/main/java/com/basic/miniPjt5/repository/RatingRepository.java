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

    // 특정 사용자가 특정 영화에 준 별점 조회
    Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId);

    // 특정 사용자가 특정 드라마에 준 별점 조회
    Optional<Rating> findByUserIdAndDramaId(Long userId, Long dramaId);

    // 특정 영화의 평균 별점 조회
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.movie.id = :movieId")
    Optional<Double> findAverageScoreByMovieId(@Param("movieId") Long movieId);

    // 특정 드라마의 평균 별점 조회
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.drama.id = :dramaId")
    Optional<Double> findAverageScoreByDramaId(@Param("dramaId") Long dramaId);

    // 특정 영화의 별점 개수 조회
    long countByMovieId(Long movieId);

    // 특정 드라마의 별점 개수 조회
    long countByDramaId(Long dramaId);

    // 특정 영화의 별점 목록 조회 (페이징)
    Page<Rating> findByMovieIdOrderByCreatedAtDesc(Long movieId, Pageable pageable);

    // 특정 드라마의 별점 목록 조회 (페이징)
    Page<Rating> findByDramaIdOrderByCreatedAtDesc(Long dramaId, Pageable pageable);

    // 특정 사용자의 별점 목록 조회 (페이징)
    Page<Rating> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 특정 사용자가 작성한 별점인지 확인
    boolean existsByIdAndUserId(Long ratingId, Long userId);

    // 별점 분포 조회 (특정 영화)
    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.movie.id = :movieId GROUP BY r.score ORDER BY r.score")
    Object[][] findScoreDistributionByMovieId(@Param("movieId") Long movieId);

    // 별점 분포 조회 (특정 드라마)
    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.drama.id = :dramaId GROUP BY r.score ORDER BY r.score")
    Object[][] findScoreDistributionByDramaId(@Param("dramaId") Long dramaId);

    // === 평점 통계 관련 추가 쿼리들 ===

    // 평점 높은 영화 목록 (평점 3개 이상인 작품만)
    @Query(value = """
        SELECT m.movie_id, 'MOVIE' as content_type, m.title, m.poster_path, 
               AVG(r.score) as avg_score, COUNT(r.rating_id) as rating_count
        FROM movies m 
        INNER JOIN ratings r ON m.movie_id = r.movie_id 
        GROUP BY m.movie_id, m.title, m.poster_path 
        HAVING COUNT(r.rating_id) >= 3 
        ORDER BY avg_score DESC, rating_count DESC
        """, nativeQuery = true)
    Page<Object[]> findTopRatedMovies(Pageable pageable);

    // 평점 높은 드라마 목록 (평점 3개 이상인 작품만)
    @Query(value = """
        SELECT d.drama_id, 'DRAMA' as content_type, d.title, d.poster_path, 
               AVG(r.score) as avg_score, COUNT(r.rating_id) as rating_count
        FROM dramas d 
        INNER JOIN ratings r ON d.drama_id = r.drama_id 
        GROUP BY d.drama_id, d.title, d.poster_path 
        HAVING COUNT(r.rating_id) >= 3 
        ORDER BY avg_score DESC, rating_count DESC
        """, nativeQuery = true)
    Page<Object[]> findTopRatedDramas(Pageable pageable);

    // 최근 1주일간 평점이 많이 등록된 영화들
    @Query(value = """
        SELECT m.movie_id, m.title, m.poster_path, AVG(r.score) as avg_score, COUNT(r.rating_id) as recent_count
        FROM movies m 
        INNER JOIN ratings r ON m.movie_id = r.movie_id 
        WHERE r.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        GROUP BY m.movie_id, m.title, m.poster_path 
        ORDER BY recent_count DESC, avg_score DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findRecentlyPopularMovies(@Param("limit") int limit);

    // 최근 1주일간 평점이 많이 등록된 드라마들
    @Query(value = """
        SELECT d.drama_id, d.title, d.poster_path, AVG(r.score) as avg_score, COUNT(r.rating_id) as recent_count
        FROM dramas d 
        INNER JOIN ratings r ON d.drama_id = r.drama_id 
        WHERE r.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        GROUP BY d.drama_id, d.title, d.poster_path 
        ORDER BY recent_count DESC, avg_score DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findRecentlyPopularDramas(@Param("limit") int limit);

    // 영화 장르별 평균 평점
    @Query(value = """
        SELECT g.name as genre_name, AVG(r.score) as avg_score, COUNT(DISTINCT m.movie_id) as movie_count
        FROM genres g 
        INNER JOIN movie_genre mg ON g.genre_id = mg.genre_id 
        INNER JOIN movies m ON mg.movie_id = m.movie_id 
        INNER JOIN ratings r ON m.movie_id = r.movie_id 
        GROUP BY g.genre_id, g.name 
        ORDER BY avg_score DESC
        """, nativeQuery = true)
    List<Object[]> findAverageRatingByMovieGenre();

    // 드라마 장르별 평균 평점
    @Query(value = """
        SELECT g.name as genre_name, AVG(r.score) as avg_score, COUNT(DISTINCT d.drama_id) as drama_count
        FROM genres g 
        INNER JOIN drama_genre dg ON g.genre_id = dg.genre_id 
        INNER JOIN dramas d ON dg.drama_id = d.drama_id 
        INNER JOIN ratings r ON d.drama_id = r.drama_id 
        GROUP BY g.genre_id, g.name 
        ORDER BY avg_score DESC
        """, nativeQuery = true)
    List<Object[]> findAverageRatingByDramaGenre();

    // 사용자별 평균 평점
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.user.id = :userId")
    Optional<Double> findAverageScoreByUserId(@Param("userId") Long userId);

    // 사용자 평점 개수
    long countByUserId(Long userId);

    // 사용자 평점 분포
    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.user.id = :userId GROUP BY r.score ORDER BY r.score")
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
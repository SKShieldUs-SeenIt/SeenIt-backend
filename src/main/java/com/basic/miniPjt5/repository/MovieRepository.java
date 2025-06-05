package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;  // ✅ JPA Repository
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByTmdbId(Long tmdbId);
    Optional<Movie> findByTmdbId(Long tmdbId);

    // 제목으로 검색 (대소문자 구분 없음)
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Movie> findByTitleContainingIgnoreCase(@Param("title") String title);

    // 페이징 지원 제목 검색
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Movie> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    // 기본 검색
    List<Movie> findByTitleContaining(String title);

    // 평점 기준 정렬
    List<Movie> findTop20ByOrderByVoteAverageDesc();
    List<Movie> findTop20ByOrderByVoteCountDesc();

    // 장르별 영화 검색
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    List<Movie> findByGenreId(@Param("genreId") Long genreId);

    // 장르별 영화 검색 (페이징)
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Page<Movie> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    // 개봉일 기준 검색
    @Query("SELECT m FROM Movie m WHERE m.releaseDate LIKE CONCAT(:year, '%')")
    List<Movie> findByReleaseYear(@Param("year") String year);

    // 여러 장르 검색
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.id IN :genreIds")
    Page<Movie> findByGenres_IdIn(@Param("genreIds") List<Long> genreIds, Pageable pageable);

    // 평점 범위 검색
    Page<Movie> findByVoteAverageBetween(Double minRating, Double maxRating, Pageable pageable);

    // 복합 검색 (제목 + 장르)
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND g.id IN :genreIds")
    Page<Movie> findByTitleContainingIgnoreCaseAndGenres_IdIn(
            @Param("title") String title,
            @Param("genreIds") List<Long> genreIds,
            Pageable pageable);
}
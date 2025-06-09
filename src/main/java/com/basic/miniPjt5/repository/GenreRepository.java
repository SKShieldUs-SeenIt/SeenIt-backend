package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 사용
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {  // ✅ JpaRepository

    Optional<Genre> findByName(String name);
    List<Genre> findByNameContainingIgnoreCase(String name);

    Boolean existsByName(String name);

    // 특정 장르의 영화/드라마 수 조회
    @Query("SELECT COUNT(m) FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Long countMoviesByGenreId(@Param("genreId") Long genreId);

    @Query("SELECT COUNT(d) FROM Drama d JOIN d.genres g WHERE g.id = :genreId")
    Long countDramasByGenreId(@Param("genreId") Long genreId);

    // 장르별 평균 평점 조회
    @Query("SELECT AVG(m.voteAverage) FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Double getAverageMovieRatingByGenreId(@Param("genreId") Long genreId);

    @Query("SELECT AVG(d.voteAverage) FROM Drama d JOIN d.genres g WHERE g.id = :genreId")
    Double getAverageDramaRatingByGenreId(@Param("genreId") Long genreId);
}
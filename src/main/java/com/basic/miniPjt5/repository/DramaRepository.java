package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Drama;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;  // ✅ JPA Repository
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DramaRepository extends JpaRepository<Drama, Long> {  // ✅ JpaRepository

    boolean existsByTmdbId(Long tmdbId);
    Optional<Drama> findByTmdbId(Long tmdbId);

    // 제목으로 검색
    @Query("SELECT d FROM Drama d WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Drama> findByTitleContainingIgnoreCase(@Param("title") String title);

    List<Drama> findByTitleContaining(String title);

    // 평점 기준 정렬
    List<Drama> findTop20ByOrderByVoteAverageDesc();
    List<Drama> findTop20ByOrderByVoteCountDesc();

    // 장르별 드라마 검색
    @Query("SELECT DISTINCT d FROM Drama d JOIN d.genres g WHERE g.id = :genreId")
    List<Drama> findByGenreId(@Param("genreId") Long genreId);

    // 방송 시작년도 기준 검색
    @Query("SELECT d FROM Drama d WHERE d.firstAirDate LIKE CONCAT(:year, '%')")
    List<Drama> findByFirstAirYear(@Param("year") String year);

    // 시즌 수 기준 검색
    List<Drama> findByNumberOfSeasonsGreaterThan(Integer seasons);

    // 페이징
    Page<Drama> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Drama> findByGenres_Id(Long genreId, Pageable pageable);
}
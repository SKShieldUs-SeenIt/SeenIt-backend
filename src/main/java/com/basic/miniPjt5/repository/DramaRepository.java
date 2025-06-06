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

    // 페이징 지원 제목 검색
    @Query("SELECT d FROM Drama d WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Drama> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

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
    Page<Drama> findByNumberOfSeasonsBetween(Integer minSeasons, Integer maxSeasons, Pageable pageable);

    // 페이징 지원 메서드들
    Page<Drama> findByGenres_Id(Long genreId, Pageable pageable);

    // 여러 장르 검색
    @Query("SELECT DISTINCT d FROM Drama d JOIN d.genres g WHERE g.id IN :genreIds")
    Page<Drama> findByGenres_IdIn(@Param("genreIds") List<Long> genreIds, Pageable pageable);

    // 평점 범위 검색
    Page<Drama> findByVoteAverageBetween(Double minRating, Double maxRating, Pageable pageable);

    // ⭐ 통합 평점 기준 정렬된 드라마 목록
    @Query("SELECT d FROM Drama d WHERE d.combinedRating IS NOT NULL ORDER BY d.combinedRating DESC")
    List<Drama> findTop20ByOrderByCombinedRatingDesc();

    // ⭐ 통합 평점 범위 검색
    @Query("SELECT d FROM Drama d WHERE COALESCE(d.combinedRating, d.voteAverage/2) BETWEEN :minRating AND :maxRating")
    Page<Drama> findByCombinedRatingBetween(@Param("minRating") Double minRating,
                                            @Param("maxRating") Double maxRating,
                                            Pageable pageable);

    @Query("SELECT DISTINCT d FROM Drama d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
            "COALESCE(d.combinedRating, d.voteAverage/2) BETWEEN :minRating AND :maxRating")
    Page<Drama> findByTitleContainingIgnoreCaseAndCombinedRatingBetween(
            @Param("title") String title,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,
            Pageable pageable
    );

    // 장르 + 통합평점 범위 검색
    @Query("SELECT DISTINCT d FROM Drama d JOIN d.genres g WHERE " +
            "g.id IN :genreIds AND " +
            "COALESCE(d.combinedRating, d.voteAverage/2) BETWEEN :minRating AND :maxRating")
    Page<Drama> findByGenres_IdInAndCombinedRatingBetween(
            @Param("genreIds") List<Long> genreIds,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,
            Pageable pageable
    );

    // 🏆 최고급 복합 검색: 제목 + 장르 + 통합평점
    @Query("SELECT DISTINCT d FROM Drama d JOIN d.genres g WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
            "g.id IN :genreIds AND " +
            "COALESCE(d.combinedRating, d.voteAverage/2) BETWEEN :minRating AND :maxRating")
    Page<Drama> findByTitleContainingIgnoreCaseAndGenres_IdInAndCombinedRatingBetween(
            @Param("title") String title,
            @Param("genreIds") List<Long> genreIds,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,
            Pageable pageable
    );

    // 제목 + 시즌수 범위 검색
    @Query("SELECT DISTINCT d FROM Drama d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
            "d.numberOfSeasons BETWEEN :minSeasons AND :maxSeasons")
    Page<Drama> findByTitleContainingIgnoreCaseAndNumberOfSeasonsBetween(
            @Param("title") String title,
            @Param("minSeasons") Integer minSeasons,
            @Param("maxSeasons") Integer maxSeasons,
            Pageable pageable
    );

    // 제목 + 방영년도 복합 검색
    @Query("SELECT DISTINCT d FROM Drama d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
            "d.firstAirDate LIKE CONCAT(:year, '%')")
    Page<Drama> findByTitleContainingIgnoreCaseAndFirstAirYear(
            @Param("title") String title,
            @Param("year") String year,
            Pageable pageable
    );

    // 장르 + 시즌수 범위 검색
    @Query("SELECT DISTINCT d FROM Drama d JOIN d.genres g WHERE " +
            "g.id IN :genreIds AND " +
            "d.numberOfSeasons BETWEEN :minSeasons AND :maxSeasons")
    Page<Drama> findByGenres_IdInAndNumberOfSeasonsBetween(
            @Param("genreIds") List<Long> genreIds,
            @Param("minSeasons") Integer minSeasons,
            @Param("maxSeasons") Integer maxSeasons,
            Pageable pageable
    );

    // 방영년도별 검색 (페이징 지원)
    @Query("SELECT d FROM Drama d WHERE d.firstAirDate LIKE CONCAT(:year, '%')")
    Page<Drama> findByFirstAirYear(@Param("year") String year, Pageable pageable);

    // 제목 + 장르 복합 검색 (기존에 없다면 추가)
    @Query("SELECT DISTINCT d FROM Drama d JOIN d.genres g WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
            "g.id IN :genreIds")
    Page<Drama> findByTitleContainingIgnoreCaseAndGenres_IdIn(
            @Param("title") String title,
            @Param("genreIds") List<Long> genreIds,
            Pageable pageable
    );

    // 드라마만의 추가 메서드
    @Query("SELECT DISTINCT d FROM Drama d JOIN d.genres g WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
            "g.id IN :genreIds AND " +
            "d.numberOfSeasons BETWEEN :minSeasons AND :maxSeasons")
    Page<Drama> findByTitleAndGenresAndSeasons(
            @Param("title") String title,
            @Param("genreIds") List<Long> genreIds,
            @Param("minSeasons") Integer minSeasons,
            @Param("maxSeasons") Integer maxSeasons,
            Pageable pageable
    );
}
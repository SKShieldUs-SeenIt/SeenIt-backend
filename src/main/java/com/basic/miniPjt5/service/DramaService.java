package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.DramaDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.mapper.DramaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DramaService {

    private final DramaRepository dramaRepository;
    private final GenreRepository genreRepository;
    private final DramaMapper dramaMapper;
    private final ContentSearchService contentSearchService;

    private final RatingService ratingService; // 🆕 추가

    // 🔥 수정된 드라마 목록 조회
    public Page<DramaDTO.ListResponse> getDramas(int page, int size, String sortBy, String sortDirection) {
        String validatedSortBy = validateAndConvertSortBy(sortBy);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), validatedSortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Drama> dramaPage = dramaRepository.findAll(pageable);

        // 🔥 수정: Repository 기반 평점 계산
        if (!dramaPage.getContent().isEmpty()) {
            for (Drama drama : dramaPage.getContent()) {
                Double newRating = ratingService.calculateDramaCombinedRating(drama.getId());
                drama.setCombinedRating(newRating);
            }
            dramaRepository.saveAll(dramaPage.getContent());
        }

        return dramaPage.map(dramaMapper::toListResponse);
    }

    // 🔥 수정된 드라마 상세 조회
    public DramaDTO.Response getDramaById(Long id) {
        Drama drama = dramaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

        // Repository 기반 평점 계산
        Double newRating = ratingService.calculateDramaCombinedRating(id);
        drama.setCombinedRating(newRating);
        dramaRepository.save(drama);

        return dramaMapper.toResponse(drama);
    }

    // 🔥 수정된 드라마 생성
    @Transactional
    public DramaDTO.Response createDrama(DramaDTO.CreateRequest request) {
        if (dramaRepository.existsByTmdbId(request.getTmdbId())) {
            throw new BusinessException(ErrorCode.DRAMA_ALREADY_EXISTS);
        }

        Drama drama = dramaMapper.toEntity(request);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            List<Genre> genres = genreRepository.findAllById(request.getGenreIds());
            if (genres.size() != request.getGenreIds().size()) {
                throw new BusinessException(ErrorCode.GENRE_NOT_FOUND);
            }
            drama.setGenres(genres);
        }

        Drama savedDrama = dramaRepository.save(drama);

        // 🔥 Repository 기반 평점 계산
        Double newRating = ratingService.calculateDramaCombinedRating(savedDrama.getId());
        savedDrama.setCombinedRating(newRating);
        dramaRepository.save(savedDrama);

        return dramaMapper.toResponse(savedDrama);
    }

    // 🔥 수정된 드라마 수정
    @Transactional
    public DramaDTO.Response updateDrama(Long id, DramaDTO.UpdateRequest request) {
        Drama drama = dramaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

        updateDramaFields(drama, request);

        // 장르 업데이트
        if (request.getGenreIds() != null) {
            List<Genre> genres = genreRepository.findAllById(request.getGenreIds());
            if (genres.size() != request.getGenreIds().size()) {
                throw new BusinessException(ErrorCode.GENRE_NOT_FOUND);
            }
            drama.setGenres(genres);
        }

        // 🔥 Repository 기반 평점 계산
        Double newRating = ratingService.calculateDramaCombinedRating(id);
        drama.setCombinedRating(newRating);

        Drama updatedDrama = dramaRepository.save(drama);
        return dramaMapper.toResponse(updatedDrama);
    }

    // 🔥 수정된 드라마 검색
    public Page<DramaDTO.ListResponse> searchDramas(DramaDTO.SearchRequest searchRequest, int page, int size) {
        Page<Drama> localResults = performLocalSearch(searchRequest, page, size);

        // 🔥 수정: Repository 기반 평점 계산
        if (!localResults.getContent().isEmpty()) {
            for (Drama drama : localResults.getContent()) {
                Double newRating = ratingService.calculateDramaCombinedRating(drama.getId());
                drama.setCombinedRating(newRating);
            }
            dramaRepository.saveAll(localResults.getContent());
        }

        if (localResults.getTotalElements() < 10 || searchRequest.getTitle() != null) {
            if (contentSearchService != null) {
                contentSearchService.searchAndSaveDramas(searchRequest.getTitle(), page);
                localResults = performLocalSearch(searchRequest, page, size);
            }
        }

        return localResults.map(dramaMapper::toListResponse);
    }

    // 🔥 완전히 새로운 안전한 평점 수정 메서드
    @Transactional
    public void fixAllCombinedRatings() {
        List<Drama> allDramas = dramaRepository.findAll();

        for (Drama drama : allDramas) {
            try {
                // 🔥 Repository 기반 계산 (컬렉션 참조 X)
                Double newRating = ratingService.calculateDramaCombinedRating(drama.getId());
                drama.setCombinedRating(newRating);

                System.out.println("드라마 ID " + drama.getId() + " (" + drama.getTitle() + ") - " +
                        "combinedRating: " + drama.getCombinedRating());
            } catch (Exception e) {
                System.err.println("드라마 ID " + drama.getId() + " 업데이트 실패: " + e.getMessage());
            }
        }

        dramaRepository.saveAll(allDramas);
        System.out.println("모든 드라마 combinedRating 업데이트 완료!");
    }

    // 영화 삭제 (관리자용)
    @Transactional
    public void deleteDrama(Long id) {
        if (!dramaRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
        }
        dramaRepository.deleteById(id);
    }

    // 평점 높은 드라마 조회
    public List<DramaDTO.ListResponse> getTopRatedDramas() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("combinedRating").descending());
        Page<Drama> dramaPage = dramaRepository.findAll(pageable);
        return dramaPage.getContent().stream()
                .map(dramaMapper::toListResponse)
                .toList();
    }

    // 시즌 수 기준 드라마 조회
    public List<DramaDTO.ListResponse> getDramasBySeasons(Integer minSeasons) {
        List<Drama> dramas = dramaRepository.findByNumberOfSeasonsGreaterThan(minSeasons);
        return dramas.stream()
                .map(dramaMapper::toListResponse)
                .toList();
    }

    private Page<Drama> performLocalSearch(DramaDTO.SearchRequest searchRequest, int page, int size) {
        String validatedSortBy = validateAndConvertSortBy(searchRequest.getSortBy());
        Sort sort = Sort.by(
                Sort.Direction.fromString(searchRequest.getSortDirection()),
                validatedSortBy
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        // 🎯 핵심 조건들만 사용한 복합 검색
        boolean hasTitle = searchRequest.getTitle() != null && !searchRequest.getTitle().trim().isEmpty();
        boolean hasGenres = searchRequest.getGenreIds() != null && !searchRequest.getGenreIds().isEmpty();
        boolean hasRating = searchRequest.getMinRating() != null || searchRequest.getMaxRating() != null;
        boolean hasSeasons = searchRequest.getMinSeasons() != null || searchRequest.getMaxSeasons() != null;

        // 1. 🏆 최고급 검색: 제목 + 장르 + 평점
        if (hasTitle && hasGenres && hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;

            return dramaRepository.findByTitleContainingIgnoreCaseAndGenres_IdInAndCombinedRatingBetween(
                    searchRequest.getTitle(),
                    searchRequest.getGenreIds(),
                    minRating,
                    maxRating,
                    pageable
            );
        }

        // 2. 제목 + 장르 + 시즌수
        else if (hasTitle && hasGenres && hasSeasons) {
            Integer minSeasons = searchRequest.getMinSeasons() != null ? searchRequest.getMinSeasons() : 1;
            Integer maxSeasons = searchRequest.getMaxSeasons() != null ? searchRequest.getMaxSeasons() : 50;

            return dramaRepository.findByTitleAndGenresAndSeasons(
                    searchRequest.getTitle(),
                    searchRequest.getGenreIds(),
                    minSeasons,
                    maxSeasons,
                    pageable
            );
        }

        // 3. 제목 + 장르
        else if (hasTitle && hasGenres) {
            return dramaRepository.findByTitleContainingIgnoreCaseAndGenres_IdIn(
                    searchRequest.getTitle(),
                    searchRequest.getGenreIds(),
                    pageable
            );
        }

        // 4. 제목 + 평점
        else if (hasTitle && hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;

            return dramaRepository.findByTitleContainingIgnoreCaseAndCombinedRatingBetween(
                    searchRequest.getTitle(),
                    minRating,
                    maxRating,
                    pageable
            );
        }

        // 5. 제목 + 시즌수
        else if (hasTitle && hasSeasons) {
            Integer minSeasons = searchRequest.getMinSeasons() != null ? searchRequest.getMinSeasons() : 1;
            Integer maxSeasons = searchRequest.getMaxSeasons() != null ? searchRequest.getMaxSeasons() : 50;

            return dramaRepository.findByTitleContainingIgnoreCaseAndNumberOfSeasonsBetween(
                    searchRequest.getTitle(),
                    minSeasons,
                    maxSeasons,
                    pageable
            );
        }

        // 6. 장르 + 평점
        else if (hasGenres && hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;

            return dramaRepository.findByGenres_IdInAndCombinedRatingBetween(
                    searchRequest.getGenreIds(),
                    minRating,
                    maxRating,
                    pageable
            );
        }

        // 7. 장르 + 시즌수
        else if (hasGenres && hasSeasons) {
            Integer minSeasons = searchRequest.getMinSeasons() != null ? searchRequest.getMinSeasons() : 1;
            Integer maxSeasons = searchRequest.getMaxSeasons() != null ? searchRequest.getMaxSeasons() : 50;

            return dramaRepository.findByGenres_IdInAndNumberOfSeasonsBetween(
                    searchRequest.getGenreIds(),
                    minSeasons,
                    maxSeasons,
                    pageable
            );
        }

        // 8. 단일 조건들
        else if (hasTitle) {
            return dramaRepository.findByTitleContainingIgnoreCase(searchRequest.getTitle(), pageable);
        }
        else if (hasGenres) {
            return dramaRepository.findByGenres_IdIn(searchRequest.getGenreIds(), pageable);
        }
        else if (hasRating) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 5.0;
            return dramaRepository.findByCombinedRatingBetween(minRating, maxRating, pageable);
        }
        else if (hasSeasons) {
            Integer minSeasons = searchRequest.getMinSeasons() != null ? searchRequest.getMinSeasons() : 1;
            Integer maxSeasons = searchRequest.getMaxSeasons() != null ? searchRequest.getMaxSeasons() : 50;
            return dramaRepository.findByNumberOfSeasonsBetween(minSeasons, maxSeasons, pageable);
        }

        // 9. 조건 없으면 통합 평점 순으로 전체 조회
        else {
            return dramaRepository.findAll(pageable);
        }
    }

    private String validateAndConvertSortBy(String sortBy) {
        if (sortBy == null) {
            return "combinedRating"; // 통합 평점을 기본값으로
        }

        switch (sortBy.toLowerCase()) {
            case "rating":
                return "combinedRating";      // 통합 평점
            case "tmdbrating":
                return "voteAverage";         // TMDB 평점
            case "title":
                return "title";
            case "firstairdate":
                return "firstAirDate";
            case "numberofseasons":
                return "numberOfSeasons";
            case "numberofepisodes":
                return "numberOfEpisodes";
            case "votecount":
                return "voteCount";
            case "voteaverage":
                return "voteAverage";
            default:
                return "combinedRating";      // 기본값
        }
    }

    private void updateDramaFields(Drama drama, DramaDTO.UpdateRequest request) {
        if (request.getTitle() != null) {
            drama.setTitle(request.getTitle());
        }
        if (request.getOverview() != null) {
            drama.setOverview(request.getOverview());
        }
        if (request.getFirstAirDate() != null) {
            drama.setFirstAirDate(request.getFirstAirDate());
        }
        if (request.getPosterPath() != null) {
            drama.setPosterPath(request.getPosterPath());
        }
        if (request.getVoteAverage() != null) {
            drama.setVoteAverage(request.getVoteAverage());
        }
        if (request.getVoteCount() != null) {
            drama.setVoteCount(request.getVoteCount());
        }
    }

}
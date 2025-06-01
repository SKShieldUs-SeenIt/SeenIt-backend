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

    // 드라마 목록 조회
    public Page<DramaDTO.ListResponse> getDramas(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Drama> dramaPage = dramaRepository.findAll(pageable);
        return dramaPage.map(dramaMapper::toListResponse);
    }

    // 드라마 상세 조회
    public DramaDTO.Response getDramaById(Long id) {
        Drama drama = dramaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

        return dramaMapper.toResponse(drama);
    }

    // 드라마 생성
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
        return dramaMapper.toResponse(savedDrama);
    }

    // 드라마 검색
    public Page<DramaDTO.ListResponse> searchDramas(DramaDTO.SearchRequest searchRequest, int page, int size) {
        Page<Drama> localResults = performLocalSearch(searchRequest, page, size);

        if (localResults.getTotalElements() < 10 || searchRequest.getTitle() != null) {
            if (contentSearchService != null) {
                contentSearchService.searchAndSaveDramas(searchRequest.getTitle(), page);
                localResults = performLocalSearch(searchRequest, page, size);
            }
        }

        return localResults.map(dramaMapper::toListResponse);
    }

    // 평점 높은 드라마 조회
    public List<DramaDTO.ListResponse> getTopRatedDramas() {
        List<Drama> dramas = dramaRepository.findTop20ByOrderByVoteAverageDesc();
        return dramas.stream()
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
        Sort sort = Sort.by(
                Sort.Direction.fromString(searchRequest.getSortDirection()),
                searchRequest.getSortBy()
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        if (searchRequest.getTitle() != null && !searchRequest.getTitle().trim().isEmpty()) {
            return dramaRepository.findByTitleContainingIgnoreCase(searchRequest.getTitle(), pageable);
        } else if (searchRequest.getGenreIds() != null && !searchRequest.getGenreIds().isEmpty()) {
            return dramaRepository.findByGenres_IdIn(searchRequest.getGenreIds(), pageable);
        } else if (searchRequest.getMinRating() != null || searchRequest.getMaxRating() != null) {
            Double minRating = searchRequest.getMinRating() != null ? searchRequest.getMinRating() : 0.0;
            Double maxRating = searchRequest.getMaxRating() != null ? searchRequest.getMaxRating() : 10.0;
            return dramaRepository.findByVoteAverageBetween(minRating, maxRating, pageable);
        } else {
            return dramaRepository.findAll(pageable);
        }
    }
}
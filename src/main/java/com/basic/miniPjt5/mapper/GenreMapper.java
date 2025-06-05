package com.basic.miniPjt5.mapper;

import com.basic.miniPjt5.DTO.GenreDTO;
import com.basic.miniPjt5.entity.Genre;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenreMapper {

    // Entity -> Response DTO
    public GenreDTO.Response toResponse(Genre genre) {
        int movieCount = genre.getMovies() != null ? genre.getMovies().size() : 0;
        int dramaCount = genre.getDramas() != null ? genre.getDramas().size() : 0;

        // 평균 평점 계산
        Double averageRating = null;
        if (movieCount > 0 || dramaCount > 0) {
            double totalRating = 0.0;
            int totalCount = 0;

            if (genre.getMovies() != null) {
                totalRating += genre.getMovies().stream()
                        .mapToDouble(movie -> movie.getVoteAverage())
                        .sum();
                totalCount += movieCount;
            }

            if (genre.getDramas() != null) {
                totalRating += genre.getDramas().stream()
                        .mapToDouble(drama -> drama.getVoteAverage())
                        .sum();
                totalCount += dramaCount;
            }

            averageRating = totalCount > 0 ? totalRating / totalCount : 0.0;
        }

        return GenreDTO.Response.builder()
                .id(genre.getId())
                .name(genre.getName())
                .movieCount(movieCount)
                .dramaCount(dramaCount)
                .totalCount(movieCount + dramaCount)
                .averageRating(averageRating)
                .build();
    }

    // Entity -> ListResponse DTO
    public GenreDTO.ListResponse toListResponse(Genre genre) {
        int totalCount = (genre.getMovies() != null ? genre.getMovies().size() : 0) +
                (genre.getDramas() != null ? genre.getDramas().size() : 0);

        return GenreDTO.ListResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .totalCount(totalCount)
                .build();
    }

    // 리스트 변환
    public List<GenreDTO.Response> toResponseList(List<Genre> genres) {
        return genres.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<GenreDTO.ListResponse> toListResponseList(List<Genre> genres) {
        return genres.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }
}
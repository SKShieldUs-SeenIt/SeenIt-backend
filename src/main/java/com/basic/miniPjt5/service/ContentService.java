package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.ContentDTO;
import com.basic.miniPjt5.DTO.DramaDTO;
import com.basic.miniPjt5.DTO.MovieDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContentService {

    private final MovieService movieService;
    private final DramaService dramaService;

    public ContentService(MovieService movieService, DramaService dramaService) {
        this.movieService = movieService;
        this.dramaService = dramaService;
    }
    //통합검색
    public ContentDTO.SearchResult searchContent(ContentDTO.SearchRequest searchRequest, int page, int size) {
        List<MovieDTO.ListResponse> movies;
        List<DramaDTO.ListResponse> dramas;
        int totalMovieResults;
        int totalDramaResults;

        if ("MOVIE".equals(searchRequest.getContentType())) {
            // 영화만 검색
            MovieDTO.SearchRequest movieSearch = convertToMovieSearch(searchRequest);
            Page<MovieDTO.ListResponse> movieResults = movieService.searchMovies(movieSearch, page, size);

            movies = movieResults.getContent();
            dramas = List.of();
            totalMovieResults = (int) movieResults.getTotalElements();
            totalDramaResults = 0;

        } else if ("DRAMA".equals(searchRequest.getContentType())) {
            // 드라마만 검색
            DramaDTO.SearchRequest dramaSearch = convertToDramaSearch(searchRequest);
            Page<DramaDTO.ListResponse> dramaResults = dramaService.searchDramas(dramaSearch, page, size);

            movies = List.of();
            dramas = dramaResults.getContent();
            totalMovieResults = 0;
            totalDramaResults = (int) dramaResults.getTotalElements();

        } else {
            // 모든 컨텐츠 검색
            MovieDTO.SearchRequest movieSearch = convertToMovieSearch(searchRequest);
            DramaDTO.SearchRequest dramaSearch = convertToDramaSearch(searchRequest);

            Page<MovieDTO.ListResponse> movieResults = movieService.searchMovies(movieSearch, page, size);
            Page<DramaDTO.ListResponse> dramaResults = dramaService.searchDramas(dramaSearch, page, size);

            movies = movieResults.getContent();
            dramas = dramaResults.getContent();
            totalMovieResults = (int) movieResults.getTotalElements();
            totalDramaResults = (int) dramaResults.getTotalElements();
        }

        return ContentDTO.SearchResult.builder()
                .query(searchRequest.getQuery())
                .movies(movies)
                .dramas(dramas)
                .totalMovieResults(totalMovieResults)
                .totalDramaResults(totalDramaResults)
                .totalResults(totalMovieResults + totalDramaResults)
                .currentPage(page)
                .build();
    }

    private MovieDTO.SearchRequest convertToMovieSearch(ContentDTO.SearchRequest request) {
        return MovieDTO.SearchRequest.builder()
                .title(request.getQuery())
                .genreIds(request.getGenreIds())
                .minRating(request.getMinRating())
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .build();
    }

    private DramaDTO.SearchRequest convertToDramaSearch(ContentDTO.SearchRequest request) {
        return DramaDTO.SearchRequest.builder()
                .title(request.getQuery())
                .genreIds(request.getGenreIds())
                .minRating(request.getMinRating())
                .sortBy(request.getSortBy())
                .sortDirection(request.getSortDirection())
                .build();
    }
}
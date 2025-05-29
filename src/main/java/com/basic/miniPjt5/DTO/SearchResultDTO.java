package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private List<Movie> movies;
    private List<Drama> dramas;
    private String query;
    private int totalResults;
    
    // 추가 메타데이터
    private int movieCount;
    private int dramaCount;
    private boolean hasMoreResults;
    private int currentPage;
    
    // 편의 메서드들
    public int getMovieCount() {
        return movies != null ? movies.size() : 0;
    }
    
    public int getDramaCount() {
        return dramas != null ? dramas.size() : 0;
    }
    
    public boolean isEmpty() {
        return getMovieCount() == 0 && getDramaCount() == 0;
    }
}
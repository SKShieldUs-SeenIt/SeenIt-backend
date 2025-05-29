package com.basic.miniPjt5.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TMDBMovieResponse {
    private List<TMDBMovie> results;
    private int totalPages;
    private int totalResults;
    
    // getters and setters
    public List<TMDBMovie> getResults() { return results; }
    public void setResults(List<TMDBMovie> results) { this.results = results; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
}
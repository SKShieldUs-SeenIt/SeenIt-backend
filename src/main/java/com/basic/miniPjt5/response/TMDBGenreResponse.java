package com.basic.miniPjt5.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TMDBGenreResponse {
    private List<TMDBGenre> genres;
    
    public List<TMDBGenre> getGenres() { return genres; }
    public void setGenres(List<TMDBGenre> genres) { this.genres = genres; }
}
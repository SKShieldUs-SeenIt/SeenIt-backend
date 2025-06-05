package com.basic.miniPjt5.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TMDBGenreResponse {
    private List<TMDBGenre> genres;
    
    public List<TMDBGenre> getGenres() { return genres; }
    public void setGenres(List<TMDBGenre> genres) { this.genres = genres; }
}
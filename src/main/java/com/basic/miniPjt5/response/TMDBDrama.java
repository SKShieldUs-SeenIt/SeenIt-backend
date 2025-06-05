package com.basic.miniPjt5.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TMDBDrama {
    private Long id;
    private String name;
    private String overview;
    @JsonProperty("first_air_date")
    private String firstAirDate;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("backdrop_path")
    private String backdropPath;
    @JsonProperty("vote_average")
    private Double voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("genre_ids")
    private List<Integer> genreIds;
    private Double popularity;
    @JsonProperty("original_language")
    private String originalLanguage;
    @JsonProperty("number_of_seasons")
    private Integer numberOfSeasons;
    @JsonProperty("number_of_episodes")
    private Integer numberOfEpisodes;
    
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }
    public String getFirstAirDate() { return firstAirDate; }
    public void setFirstAirDate(String firstAirDate) { this.firstAirDate = firstAirDate; }
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    public Double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(Double voteAverage) { this.voteAverage = voteAverage; }
    public Integer getVoteCount() { return voteCount; }
    public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }
    public List<Integer> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Integer> genreIds) { this.genreIds = genreIds; }
    public Double getPopularity() { return popularity; }
    public void setPopularity(Double popularity) { this.popularity = popularity; }
    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public Integer getNumberOfSeasons() { return numberOfSeasons; }
    public void setNumberOfSeasons(Integer numberOfSeasons) { this.numberOfSeasons = numberOfSeasons; }
    public Integer getNumberOfEpisodes() { return numberOfEpisodes; }
    public void setNumberOfEpisodes(Integer numberOfEpisodes) { this.numberOfEpisodes = numberOfEpisodes; }
}
package com.basic.miniPjt5.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tmdb")
@Data
public class TMDBProperties {
    private String apiKey;
    private String baseUrl = "https://api.themoviedb.org/3";
    private String imageBaseUrl = "https://image.tmdb.org/t/p/w500";
    private int maxPagesPerRequest = 5;
    private long apiDelayMs = 100;
}
package com.basic.miniPjt5.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TMDB 이미지 URL 생성 유틸리티
@Component
public class TMDBImageUrlUtil {
    
    @Value("${tmdb.api.image-base-url:https://image.tmdb.org/t/p/w500}")
    private String imageBaseUrl;
    
    public String getFullImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        if (imagePath.startsWith("http")) {
            return imagePath;
        }
        
        return imageBaseUrl + imagePath;
    }
    
    public String getPosterUrl(String posterPath) {
        return getFullImageUrl(posterPath);
    }
    
    public String getBackdropUrl(String backdropPath) {
        if (backdropPath == null || backdropPath.isEmpty()) {
            return null;
        }
        
        String backdropBaseUrl = imageBaseUrl.replace("w500", "w1280");
        return backdropBaseUrl + backdropPath;
    }
    
    public String getThumbnailUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        String thumbnailBaseUrl = imageBaseUrl.replace("w500", "w200");
        return thumbnailBaseUrl + imagePath;
    }
}
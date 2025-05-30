package com.basic.miniPjt5.util;

import org.springframework.stereotype.Component;

// 데이터 검증 유틸리티
@Component
public class DataValidationUtil {
    
    public boolean isValidTMDBId(Long tmdbId) {
        return tmdbId != null && tmdbId > 0;
    }
    
    public boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.length() <= 500;
    }
    
    public boolean isValidOverview(String overview) {
        return overview == null || overview.length() <= 2000;
    }
    
    public boolean isValidRating(Double rating) {
        return rating == null || (rating >= 0.0 && rating <= 10.0);
    }
    
    public boolean isValidVoteCount(Integer voteCount) {
        return voteCount == null || voteCount >= 0;
    }
    
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("\\s+", " ") // 연속된 공백을 하나로
                   .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", ""); // 제어 문자 제거
    }
}
package com.basic.miniPjt5;

import com.basic.miniPjt5.util.DataValidationUtil;
import com.basic.miniPjt5.util.TMDBImageUrlUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

// 유틸리티 클래스 테스트
@SpringBootTest
@ActiveProfiles("test")
class UtilityTest {

    @Autowired
    private DataValidationUtil dataValidationUtil;

    @Autowired
    private TMDBImageUrlUtil imageUrlUtil;

    @Test
    @DisplayName("데이터 검증 유틸리티 테스트")
    void testDataValidation() {
        // TMDB ID 검증
        assertThat(dataValidationUtil.isValidTMDBId(123L)).isTrue();
        assertThat(dataValidationUtil.isValidTMDBId(null)).isFalse();
        assertThat(dataValidationUtil.isValidTMDBId(-1L)).isFalse();

        // 제목 검증
        assertThat(dataValidationUtil.isValidTitle("Valid Title")).isTrue();
        assertThat(dataValidationUtil.isValidTitle("")).isFalse();
        assertThat(dataValidationUtil.isValidTitle(null)).isFalse();

        // 평점 검증
        assertThat(dataValidationUtil.isValidRating(8.5)).isTrue();
        assertThat(dataValidationUtil.isValidRating(0.0)).isTrue();
        assertThat(dataValidationUtil.isValidRating(10.0)).isTrue();
        assertThat(dataValidationUtil.isValidRating(-1.0)).isFalse();
        assertThat(dataValidationUtil.isValidRating(11.0)).isFalse();

        System.out.println("데이터 검증 테스트 완료");
    }

    @Test
    @DisplayName("이미지 URL 유틸리티 테스트")
    void testImageUrlUtil() {
        // given
        String posterPath = "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg";
        String backdropPath = "/backdrop123.jpg";

        // when & then
        String posterUrl = imageUrlUtil.getPosterUrl(posterPath);
        String backdropUrl = imageUrlUtil.getBackdropUrl(backdropPath);
        String thumbnailUrl = imageUrlUtil.getThumbnailUrl(posterPath);

        assertThat(posterUrl).contains("w500");
        assertThat(backdropUrl).contains("w1280");
        assertThat(thumbnailUrl).contains("w200");

        System.out.println("포스터 URL: " + posterUrl);
        System.out.println("배경 URL: " + backdropUrl);
        System.out.println("썸네일 URL: " + thumbnailUrl);
    }
}
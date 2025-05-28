package com.basic.miniPjt5.config;

import com.basic.miniPjt5.service.TMDBApiService;
import com.basic.miniPjt5.util.DataValidationUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary  // 기존 Bean을 대체
    public TMDBApiService mockTMDBApiService() {
        return Mockito.mock(TMDBApiService.class);
    }
    
    @Bean
    @Primary
    public DataValidationUtil mockDataValidationUtil() {
        return Mockito.mock(DataValidationUtil.class);
    }
}
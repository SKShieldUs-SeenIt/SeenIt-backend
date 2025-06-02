// TestSecurityConfig.java (src/main/java 또는 src/test/java/config 폴더에 생성)
package com.basic.miniPjt5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 필요할 경우 추가 (기존 설정과 충돌 확인)
@Profile("test2") // test2 프로필일 때만 이 설정이 활성화됩니다.
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (테스트용)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // 모든 요청을 인증 없이 허용
            );
        return http.build();
    }
}
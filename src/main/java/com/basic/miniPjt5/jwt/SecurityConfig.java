package com.basic.miniPjt5.config;

import com.basic.miniPjt5.jwt.JwtAuthenticationEntryPoint;
import com.basic.miniPjt5.jwt.JwtAuthenticationFilter;
import com.basic.miniPjt5.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화 (REST API 보통 비활성화함)
                .csrf(csrf -> csrf.disable())
                // 세션 사용 안함 (JWT 토큰 기반 stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증 예외처리 엔트리 포인트 등록 (401 응답 처리)
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // 요청 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 인증 예외 경로
                        .anyRequest().authenticated()                // 그 외 요청 인증 필요
                )
                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 등록하여 JWT 인증 처리
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

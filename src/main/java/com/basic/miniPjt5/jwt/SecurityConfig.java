package com.basic.miniPjt5.jwt;

import com.basic.miniPjt5.jwt.JwtAuthenticationEntryPoint;
import com.basic.miniPjt5.jwt.JwtAuthenticationFilter;
import com.basic.miniPjt5.jwt.JwtTokenProvider;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/swagger-ui",
                                "/swagger-resources", // swagger-resources 루트 경로
                                "/swagger-resources/configuration/ui", // 특정 구성 리소스
                                "/swagger-resources/configuration/security"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 🔒 관리자만 접근
                        .requestMatchers(
                                "/api/auth/**",       // 🔓 로그인, 회원가입, 카카오 로그인 등 인증 관련
                                "/api/health/**",     // 🔓 헬스 체크 (서버 상태 확인)
                                "/api/genres/**",     // 🔓 장르 목록 (드롭다운 등에서 사용)
                                "/api/content/**",    // 🔓 통합 검색 (제목 검색 등)
                                "/api/statistics/**", // 🔓 리뷰/평점 통계 등
                                "/api/movies/**",     // 🔓 영화 상세 정보, 목록
                                "/api/dramas/**"      // 🔓 드라마 상세 정보, 목록
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // JwtAuthenticationFilter에 UserRepository 주입해서 생성자 호출
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

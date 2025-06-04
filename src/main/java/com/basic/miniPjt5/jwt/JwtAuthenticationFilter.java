package com.basic.miniPjt5.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.repository.UserRepository;
import com.basic.miniPjt5.security.UserPrincipal;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository; // DB 조회용

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            log.info("Authorization header 토큰 있음: {}", token);

            if (jwtTokenProvider.validateToken(token)) {
                String userIdStr = jwtTokenProvider.getUserIdFromToken(token);

                log.info("✅ JWT 토큰에서 추출된 userId: {}", userIdStr);

                // DB에서 User 조회 후 UserPrincipal 생성
                Long userId = Long.valueOf(userIdStr);
                Optional<User> userOptional = userRepository.findById(userId);

                if (userOptional.isPresent()) {
                    UserPrincipal userPrincipal = UserPrincipal.fromUser(userOptional.get());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("✅ JWT 인증 성공, userId: {}", userId);

                } else {
                    log.warn("❌ DB에 userId에 해당하는 사용자가 없음");
                }

            } else {
                log.warn("❌ JWT 토큰 유효성 검사 실패");
            }
        } else {
            log.info("❌ Authorization 헤더에 Bearer 토큰이 없음 또는 형식 오류");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization 헤더 값: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

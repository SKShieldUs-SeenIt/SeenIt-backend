package com.basic.miniPjt5.jwt;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.repository.UserRepository;
import com.basic.miniPjt5.security.UserPrincipal;
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

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            log.info("🛡️ Authorization header 토큰 감지: {}", token);

            if (jwtTokenProvider.validateToken(token)) {
                String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
                Long userId = Long.valueOf(userIdStr);
                Optional<User> userOptional = userRepository.findById(userId);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    UserPrincipal userPrincipal = UserPrincipal.fromUser(user);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal, null, userPrincipal.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("✅ SecurityContext 인증 완료: userId = {}, role = {}", userId, user.getRole());
                } else {
                    log.warn("❌ DB에서 해당 userId 사용자를 찾을 수 없음");
                }

            } else {
                log.warn("❌ JWT 토큰 검증 실패");
            }

        } else {
            log.info("❌ Authorization 헤더 누락 또는 형식 오류");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * ✅ 인증이 필요 없는 URL을 지정하여 필터에서 제외
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/kakao")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/public")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");
    }
    /**
     * 🔍 Bearer 토큰 파싱 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("📥 Authorization 헤더 수신: {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

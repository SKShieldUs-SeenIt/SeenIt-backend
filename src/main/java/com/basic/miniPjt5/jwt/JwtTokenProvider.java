package com.basic.miniPjt5.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    private static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 7일

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // ✅ 1. 액세스 토큰 생성
    public String createAccessToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 2. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("❌ JWT 유효성 검사 실패: {}", e.getMessage());
            return false;
        }
    }

    // ✅ 3. 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}

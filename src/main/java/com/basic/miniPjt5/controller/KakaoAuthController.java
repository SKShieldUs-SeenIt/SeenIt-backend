package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;
import com.basic.miniPjt5.service.KakaoAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    // ✅ GET: 카카오 로그인 콜백 처리
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 인가 코드를 받아 access token을 발급하고 JWT를 생성하여 프론트엔드로 리다이렉트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "JWT 토큰과 함께 프론트로 리다이렉트 성공"),
            @ApiResponse(responseCode = "500", description = "로그인 처리 중 서버 오류 발생")
    })
    @GetMapping("/kakao/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) {
        log.info("✅ 로그인 성공, 카카오 인가 코드 수신 - code: {}", code);

        try {
            KakaoLoginResponse loginResponse = kakaoAuthService.login(code);
            String jwtToken = loginResponse.getAccessToken();
            boolean isNewUser = loginResponse.isNewUser();
            String redirectUrl = "http://localhost:5173/kakao/callback?token=" + jwtToken + "&isNew=" + isNewUser;

            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            log.error("❌ 로그인 처리 중 오류 발생", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 실패");
            } catch (IOException ioException) {
                log.error("❌ 응답 실패", ioException);
            }
        }
    }

    // ✅ GET: 테스트용 엔드포인트
    @Operation(summary = "API 테스트용 엔드포인트", description = "서버 연결 확인을 위한 간단한 테스트 API입니다.")
    @ApiResponse(responseCode = "200", description = "정상 응답 OK 반환")
    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}

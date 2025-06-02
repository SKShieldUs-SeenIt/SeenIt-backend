package com.basic.miniPjt5.service.impl;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;
import com.basic.miniPjt5.DTO.KakaoTokenResponse;
import com.basic.miniPjt5.DTO.KakaoUserInfo;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.jwt.JwtTokenProvider;
import com.basic.miniPjt5.service.KakaoAuthService;
import com.basic.miniPjt5.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient.Builder webClientBuilder;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public KakaoLoginResponse login(String code) {
        // 1. 카카오 토큰 요청
        KakaoTokenResponse tokenResponse = getToken(code);
        log.info("🟡 access token: {}", tokenResponse.getAccessToken());

        // 2. 사용자 정보 조회
        KakaoUserInfo userInfo = getUserInfo(tokenResponse.getAccessToken());
        log.info("🟡 user info: {}", userInfo);

        // 3. 신규 사용자 여부 판단
        String kakaoId = String.valueOf(userInfo.getId());
        boolean isNewUser = !userService.existsByKakaoId(kakaoId);

        // 4. User 객체 생성 및 저장 또는 업데이트
        User user = User.builder()
                .kakaoId(kakaoId)
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .preferredGenres(null)
                .joinDate(LocalDate.now())
                .build();

        User savedUser = userService.saveOrUpdate(user);
        log.info("✅ 사용자 저장 완료: {}", savedUser.getEmail());

        // 5. JWT 생성
        String jwtAccessToken = jwtTokenProvider.createAccessToken(savedUser.getKakaoId());

        // 6. 응답 반환
        return KakaoLoginResponse.builder()
                .accessToken(jwtAccessToken)
                .isNewUser(isNewUser)
                .build();
    }

    private KakaoTokenResponse getToken(String code) {
        WebClient webClient = webClientBuilder.build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }
}

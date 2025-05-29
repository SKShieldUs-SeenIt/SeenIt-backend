package com.basic.miniPjt5.service.impl;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;
import com.basic.miniPjt5.DTO.KakaoTokenResponse;
import com.basic.miniPjt5.DTO.KakaoUserInfo;
import com.basic.miniPjt5.service.KakaoAuthService;
import com.basic.miniPjt5.entity.User;
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

    @Override
    public KakaoLoginResponse login(String code) {
        KakaoTokenResponse tokenResponse = getToken(code);
        KakaoUserInfo userInfo = getUserInfo(tokenResponse.getAccessToken());

        // 사용자 객체 생성
        User user = User.builder()
                .kakaoId(String.valueOf(userInfo.getId()))
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .profileImageUrl(userInfo.getProfileImageUrl())  // 추가한 메서드 필요
                .preferredGenres(null)  // 초기값이 필요하면
                // status는 기본값 ACTIVE이므로 빌더에서 안 넣어도 됨
                .build();

        // DB에 저장 또는 업데이트
        User savedUser = userService.saveOrUpdate(user);
        log.info("✅ 사용자 저장 완료: {}", savedUser.getEmail());

        // 로그인 응답 반환
        return new KakaoLoginResponse(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getEmail()
        );
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

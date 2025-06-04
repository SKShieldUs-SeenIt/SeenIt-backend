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
        // 1. 카카오 토큰 요청 (getAccessToken 호출)

        String accessToken = getAccessToken(code);
        log.info("🟡 access token: {}", accessToken);

        // 2. 사용자 정보 조회 (getUserInfo 호출)
        KakaoUserInfo userInfo = getUserInfo(accessToken);
        log.info("🟡 user info: {}", userInfo);

        // 3. 신규 사용자 여부 판단
        String kakaoId = String.valueOf(userInfo.getId());
        boolean isNewUser = !userService.existsByKakaoId(kakaoId);

        // 4. User 객체 생성 및 저장 또는 업데이트
        User user = User.builder()
                .kakaoId(kakaoId)
                .email(userInfo.getEmail())  // 실제 userInfo에서 email을 가져옴
                .name(userInfo.getName())    // 실제 userInfo에서 name을 가져옴
                .profileImageUrl(userInfo.getProfileImageUrl())  // 실제 userInfo에서 profileImageUrl을 가져옴
                .preferredGenres(null)
                .joinDate(LocalDate.now())
                .build();

        User savedUser = userService.saveOrUpdate(user);
        log.info("✅ 사용자 이메일 저장 완료: {}", savedUser.getEmail());

        log.info("✅ 저장된 userId: {}", savedUser.getUserId());

        // 5. JWT 생성 (createJwtToken 호출)
        String jwtAccessToken = createJwtToken(savedUser.getUserId().toString());

        // 6. 응답 반환 (JWT 토큰 포함)
        return KakaoLoginResponse.builder()
                .accessToken(jwtAccessToken)
                .isNewUser(isNewUser)
                .build();
    }

    // 1. 카카오 액세스 토큰을 요청하는 메서드 (getAccessToken)
    @Override
    public String getAccessToken(String code) {
        WebClient webClient = webClientBuilder.build();


        // POST 요청을 통해 카카오 API에서 액세스 토큰을 받아옵니다.
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code); // 받은 인가 코드 전달
        log.info("폼 데이터: {}", formData);  // formData를 제대로 로그 찍기

        KakaoTokenResponse tokenResponse = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();  // 요청 후 응답을 동기적으로 받음


        if (tokenResponse == null) {
            throw new IllegalStateException("카카오 토큰 응답이 null입니다.");
        }

        log.info("카카오 API 응답: {}", tokenResponse);
        log.info("카카오 액세스 토큰: {}", tokenResponse.getAccessToken());
        return tokenResponse.getAccessToken();  // 액세스 토큰 반환
    }

    // 2. 카카오 사용자 정보를 가져오는 메서드 (getUserInfo)
    @Override
    public KakaoUserInfo getUserInfo(String accessToken) {
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();  // 카카오 사용자 정보 반환
    }

    // 3. JWT 토큰을 생성하는 메서드 (createJwtToken)
    @Override
    public String createJwtToken(String userId) {
        log.info("JWT 토큰 생성 시 전달된 userId: {}", userId);
        return jwtTokenProvider.createAccessToken(userId);
    }
}

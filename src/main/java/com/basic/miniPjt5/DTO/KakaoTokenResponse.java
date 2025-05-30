package com.basic.miniPjt5.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;

    @JsonProperty("scope")
    private String scope;

    // ✅ toString() 추가 (원하면 로그 찍을 때 활용 가능)
    @Override
    public String toString() {
        return "KakaoTokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshTokenExpiresIn=" + refreshTokenExpiresIn +
                ", scope='" + scope + '\'' +
                '}';
    }
}

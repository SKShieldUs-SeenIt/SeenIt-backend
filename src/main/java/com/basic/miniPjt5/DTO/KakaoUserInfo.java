package com.basic.miniPjt5.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
public class KakaoUserInfo {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("properties")
    private Properties properties;

    public String getName() {
        return properties != null ? properties.getNickname() : null;
    }

    public String getProfileImageUrl() {
        if (kakaoAccount != null && kakaoAccount.profile != null) {
            return kakaoAccount.profile.getProfileImageUrl();
        }
        return null;
    }

    // ✅ toString() 추가
    @Override
    public String toString() {
        return "KakaoUserInfo{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", profileImageUrl='" + getProfileImageUrl() + '\'' +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        @JsonProperty("profile_needs_agreement")
        private Boolean profileNeedsAgreement;

        private Profile profile;

        @JsonProperty("profile_name_needs_agreement")
        private Boolean profileNameNeedsAgreement;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class Profile {
        private String name;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class Properties {
        private String nickname;
    }
}

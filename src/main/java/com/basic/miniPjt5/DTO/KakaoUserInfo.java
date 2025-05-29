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

    public String getName() {
        if (kakaoAccount != null && kakaoAccount.profile != null) {
            return kakaoAccount.profile.getName();
        }
        return null;
    }

    public String getEmail() {
        if (kakaoAccount != null) {
            return kakaoAccount.getEmail();
        }
        return null;
    }

    public String getProfileImageUrl() {
        if (kakaoAccount != null && kakaoAccount.profile != null) {
            return kakaoAccount.profile.getProfileImageUrl();
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        @JsonProperty("profile_needs_agreement")
        private Boolean profileNeedsAgreement;

        private Profile profile;

        private String email;

        @JsonProperty("profile_name_needs_agreement")
        private Boolean profileNameNeedsAgreement;

        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class Profile {
        private String name;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}

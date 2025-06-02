package com.basic.miniPjt5.security;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 카카오 로그인을 위한 CustomUserDetails 구현
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 사용자 ID 반환 (User 엔티티의 PK)
     */
    public Long getUserId() {
        return user.getUserId();
    }

    /**
     * 카카오 ID 반환
     */
    public String getKakaoId() {
        return user.getKakaoId();
    }

    /**
     * 사용자 이름 반환
     */
    public String getName() {
        return user.getName();
    }

    /**
     * 이메일 반환
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 프로필 이미지 URL 반환
     */
    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }

    /**
     * 선호 장르 반환
     */
    public String getPreferredGenres() {
        return user.getPreferredGenres();
    }

    /**
     * User 엔티티 반환
     */
    public User getUser() {
        return user;
    }

    // === UserDetails 인터페이스 구현 ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 현재는 단순히 ROLE_USER 권한만 부여
        // 추후 관리자 권한 등이 필요하면 User 엔티티에 role 필드 추가 가능
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // 카카오 로그인이므로 패스워드는 사용하지 않음
        // 하지만 null을 반환하면 안 되므로 빈 문자열 반환
        return "";
    }

    @Override
    public String getUsername() {
        // Spring Security에서 사용자 식별을 위해 카카오 ID 반환
        return user.getKakaoId();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 - 현재는 만료되지 않음으로 설정
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부 - 현재는 잠금되지 않음으로 설정
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명 만료 여부 - 현재는 만료되지 않음으로 설정
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부 - UserStatus.ACTIVE인 경우만 활성화
        return user.getStatus() == UserStatus.ACTIVE;
    }

    // === Object 메서드 오버라이드 ===

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CustomUserDetails that = (CustomUserDetails) obj;
        return user.getUserId().equals(that.user.getUserId());
    }

    @Override
    public int hashCode() {
        return user.getUserId().hashCode();
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "userId=" + user.getUserId() +
                ", kakaoId='" + user.getKakaoId() + '\'' +
                ", name='" + user.getName() + '\'' +
                ", status=" + user.getStatus() +
                '}';
    }
}
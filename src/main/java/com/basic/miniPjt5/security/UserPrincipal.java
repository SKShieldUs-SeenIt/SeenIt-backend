package com.basic.miniPjt5.security;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String kakaoId;
    private final UserStatus status;

    public UserPrincipal(Long id, String email, String kakaoId, UserStatus status) {
        this.id = id;
        this.email = email;
        this.kakaoId = kakaoId;
        this.status = status;
    }

    public static UserPrincipal fromUser(User user) {
        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getKakaoId(),
                user.getStatus()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();  // 현재 권한 사용 안 함
    }

    @Override
    public String getPassword() {
        return null;  // 소셜 로그인이므로 비밀번호 없음
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정 만료 X
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 잠금 X
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명 만료 X
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE; // pring Security의 필수 메서드
    }

    public boolean isSuspended() {
        return status == UserStatus.SUSPENDED;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}

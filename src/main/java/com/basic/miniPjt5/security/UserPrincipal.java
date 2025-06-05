package com.basic.miniPjt5.security;

import com.basic.miniPjt5.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String kakaoId;  // 추가된 카카오ID 필드

    // 생성자에 kakaoId 추가
    public UserPrincipal(Long id, String email, String kakaoId) {
        this.id = id;
        this.email = email;
        this.kakaoId = kakaoId;
    }

    // User 엔티티로부터 UserPrincipal 생성 시 kakaoId도 함께 세팅
    public static UserPrincipal fromUser(User user) {
        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getKakaoId()   // User 엔티티에 kakaoId getter가 있어야 함
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

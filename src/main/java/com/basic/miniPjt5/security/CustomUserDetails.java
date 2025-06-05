package com.basic.miniPjt5.security;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public String getKakaoId() {
        return user.getKakaoId();
    }

    public String getName() {
        return user.getName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }

    // preferredGenres를 List<String>으로 변경
    public List<String> getPreferredGenres() {
        return user.getPreferredGenres();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return user.getKakaoId();
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
        return user.getStatus() == UserStatus.ACTIVE;
    }

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

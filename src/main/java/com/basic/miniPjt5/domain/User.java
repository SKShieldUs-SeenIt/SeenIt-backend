package com.basic.miniPjt5.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Getter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_number", columnList = "user_id"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class User {

    // 사용자 고유 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 카카오 로그인 ID (Unique, 필수)
    @Column(name = "kakao_id", nullable = false, unique = true)
    private String kakaoId;

    // 사용자 닉네임 (2~50자)
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    @Column(nullable = false, length = 50)
    private String name;

    // 이메일 (선택, unique)
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Column(nullable = true, length = 100, unique = true)
    private String email;

    // 사용자 상태 (ACTIVE, SUSPENDED, DELETED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // 선호 장르 (쉼표로 구분된 문자열 등)
    @Column(name = "preferred_genres", length = 255)
    private String preferredGenres;

    // 가입일 (자동 생성)
    @CreatedDate
    @Column(name = "join_date", nullable = false, updatable = false)
    private LocalDate joinDate;

    // 프로필 이미지 URL
    @Column(name = "profile_image_url", length = 200)
    private String profileImageUrl;

    // 생성자 (Builder 패턴 사용 추천)
    public User(String kakaoId, String name, String email, String preferredGenres, String profileImageUrl) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.email = email;
        this.status = UserStatus.ACTIVE;
        this.preferredGenres = preferredGenres;
        this.profileImageUrl = profileImageUrl;
        this.joinDate = LocalDate.now();
    }

    // 상태 변경 등 추가 메서드
    public void updateStatus(UserStatus status) {
        this.status = status;
    }
}

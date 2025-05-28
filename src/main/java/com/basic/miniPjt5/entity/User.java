package com.basic.miniPjt5.entity;

import com.basic.miniPjt5.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_number", columnList = "user_number"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {
    // 사용자 고유 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 카카오 로그인 ID (Unique, 필수)
    @Column(name = "kakao_id", nullable = false, unique = true)
    private String kakaoId;

    // 사용자 닉네임 (2~50자)
    @Column(nullable = false, length = 50)
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    private String name;

    // 이메일 (Unique, 선택 저장)
    @Column(nullable = true, length = 100, unique = true)
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;

    // 사용자 상태 (ENUM: ACTIVE, SUSPENDED, DELETED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // 선호 장르 (쉼표로 구분된 문자열 등)
    @Column(name = "preferred_genres", length = 255)
    private String preferredGenres;

    // 가입일 (기본값: 오늘 날짜)
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate = LocalDate.now();

    // 프로필 이미지 URL
    @Column(name = "profile_image_url", length = 200)
    private String profileImageUrl;

    // 리뷰 목록 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    // 게시글 목록 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    // 댓글 목록 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    // 별점 목록 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rating> ratings = new ArrayList<>();

    /**
     * 활성 사용자 여부 확인
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
    /**
     * 사용자 상태 변경
     * @param newStatus 변경할 상태
     * @param reason 변경 사유 (기록용, 미구현)
     */
    public void changeStatus(UserStatus newStatus, String reason) {
        if (this.status != newStatus) {
            this.status = newStatus;
            // TODO: 상태 변경 이력 기록 (UserStatusHistory 등)
        }
    }
    /**
     * 사용자 프로필 수정
     */
    public void updateProfile(String name, String profileImageUrl, String preferredGenres) {
        if (StringUtils.hasText(name)) this.name = name;
        if (StringUtils.hasText(profileImageUrl)) this.profileImageUrl = profileImageUrl;
        this.preferredGenres = preferredGenres;
    }
    @Builder
    private User(String userNumber, String kakaoId, String name, String email, String profileImageUrl, String preferredGenres) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.preferredGenres = preferredGenres;
        this.status = UserStatus.ACTIVE;
        this.joinDate = LocalDate.now();
    }

    /**
     * 사용자 생성 팩토리 메서드
     */
    public static User create(String kakaoId, String name, String email, String profileImageUrl, String preferredGenres) {
        return User.builder()
                .kakaoId(kakaoId)
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .preferredGenres(preferredGenres)
                .build();
    }
}


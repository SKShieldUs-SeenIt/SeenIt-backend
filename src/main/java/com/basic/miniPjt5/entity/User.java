package com.basic.miniPjt5.entity;

import com.basic.miniPjt5.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_kakao_id", columnList = "kakao_id"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private String kakaoId;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    @Column(nullable = false, length = 50)
    private String name;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Column(length = 100) // ✅ nullable 허용 시 unique 제거
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "preferred_genres", length = 255)
    private String preferredGenres;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "profile_image_url", length = 200)
    private String profileImageUrl;

    /**
     * 계정 활성 상태 확인
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * 계정 상태 변경
     */
    public void changeStatus(UserStatus newStatus, String reason) {
        if (this.status != newStatus) {
            this.status = newStatus;
            // TODO: 상태 변경 이력 저장 필요 시 구현
        }
    }

    /**
     * 사용자 프로필 수정
     */
    public void updateProfile(String name, String profileImageUrl, String preferredGenres) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.preferredGenres = preferredGenres;
    }

    /**
     * 생성 시 joinDate가 null이면 자동 할당
     */
    @PrePersist
    protected void onCreate() {
        if (this.joinDate == null) {
            this.joinDate = LocalDate.now();
        }
    }

    /**
     * 빌더
     */
    @Builder
    private User(String kakaoId, String name, String email,
                 String profileImageUrl, String preferredGenres,
                 UserStatus status, LocalDate joinDate) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.preferredGenres = preferredGenres;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.joinDate = joinDate != null ? joinDate : LocalDate.now();
    }

    /**
     * 정적 팩토리 메서드
     */
    public static User create(String kakaoId, String name, String email,
                              String profileImageUrl, String preferredGenres) {
        return User.builder()
                .kakaoId(kakaoId)
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .preferredGenres(preferredGenres)
                .status(UserStatus.ACTIVE)
                .joinDate(LocalDate.now())
                .build();
    }

    /**
     * 디버깅용 toString()
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", kakaoId='" + kakaoId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", preferredGenres='" + preferredGenres + '\'' +
                ", joinDate=" + joinDate +
                '}';
    }
}

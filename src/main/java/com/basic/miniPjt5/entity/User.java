package com.basic.miniPjt5.entity;

import com.basic.miniPjt5.converter.ListToStringConverter;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_kakao_id", columnList = "kakao_id"),
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
    private Long userId;

    // 카카오 로그인 ID (Unique, 필수)
    @NotBlank(message = "카카오 ID는 필수입니다")
    @Column(name = "kakao_id", nullable = false, unique = true)
    private String kakaoId;

    // 사용자 닉네임 (2~50자)
    @Column(nullable = false, length = 50)
    @Setter
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    private String name;

    // 이메일 (Unique, 선택 저장)
    @Setter
    @Column(length = 100, unique = true)
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;

    // 사용자 상태 (ENUM: ACTIVE, SUSPENDED, DELETED)
    @Enumerated(EnumType.STRING)
    @Setter
    @Column(nullable = false)
    private UserStatus status;

    // 선호 장르 (쉼표로 구분된 문자열 등)
    @Setter
    @Convert(converter = ListToStringConverter.class)
    @Column(name = "preferred_genres", length = 300)
    private List<String> preferredGenres = new ArrayList<>();

    // 가입일 (기본값: 오늘 날짜)
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    // 프로필 이미지 URL
    @Setter
    @Column(name = "profile_image_url", length = 200)
    private String profileImageUrl;

    //활성 상태인지 확인
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    //사용자 상태 변경
    public void changeStatus(UserStatus newStatus, String reason) {
        if (this.status != newStatus) {
            this.status = newStatus;
        }
    }

    //프로필 업데이트
    public void updateProfile(String name, String profileImageUrl, List<String> preferredGenres) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.preferredGenres = preferredGenres != null ? preferredGenres : new ArrayList<>();
    }

    //가입 날짜 자동 설정
    @PrePersist
    protected void onCreate() {
        if (this.joinDate == null) {
            this.joinDate = LocalDate.now();
        }
    }

    @Builder
    private User(String kakaoId, String name, String email,
                 String profileImageUrl, List<String> preferredGenres,
                 UserStatus status, LocalDate joinDate) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.preferredGenres = preferredGenres != null ? preferredGenres : new ArrayList<>();
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.joinDate = joinDate != null ? joinDate : LocalDate.now();
    }

    public static User create(String kakaoId, String name, String email,
                              String profileImageUrl, List<String> preferredGenres) {
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

    // 연관 관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Rating> ratings = new ArrayList<>();



    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", kakaoId='" + kakaoId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", preferredGenres=" + preferredGenres +
                ", joinDate=" + joinDate +
                '}';
    }
}

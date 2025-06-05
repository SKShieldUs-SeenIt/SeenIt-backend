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
    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // ** 변경된 부분: 문자열로 저장, Java에선 List<String> 사용 **
    @Convert(converter = ListToStringConverter.class)
    @Column(name = "preferred_genres", length = 300)
    private List<String> preferredGenres = new ArrayList<>();

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "profile_image_url", length = 200)
    private String profileImageUrl;

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public void changeStatus(UserStatus newStatus, String reason) {
        if (this.status != newStatus) {
            this.status = newStatus;
        }
    }

    public void updateProfile(String name, String profileImageUrl, List<String> preferredGenres) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.preferredGenres = preferredGenres != null ? preferredGenres : new ArrayList<>();
    }

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

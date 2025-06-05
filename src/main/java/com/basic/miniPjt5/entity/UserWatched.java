package com.basic.miniPjt5.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_watched", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "content_type", "content_id"}),
       indexes = {
           @Index(name = "idx_user_content", columnList = "user_id, content_type"),
           @Index(name = "idx_content_type_id", columnList = "content_type, content_id")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWatched {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // User의 userId 참조

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 10)
    private ContentType contentType;

    @Column(name = "content_id", nullable = false)
    private Long contentId; // Movie ID 또는 Drama ID

    @CreatedDate
    @Column(name = "watched_at", nullable = false, updatable = false)
    private LocalDateTime watchedAt;

    public enum ContentType {
        MOVIE, DRAMA
    }

    // 정적 팩토리 메서드
    public static UserWatched create(Long userId, ContentType contentType, Long contentId) {
        return UserWatched.builder()
                .userId(userId)
                .contentType(contentType)
                .contentId(contentId)
                .build();
    }
}
package com.basic.miniPjt5.entity;

import com.basic.miniPjt5.enums.ContentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_code", unique = true, nullable = false, length = 30)
    @Setter
    private String code;

    @Column(name = "post_title", nullable = false, length = 100)
    private String title;

    @Column(name = "post_content", nullable = false, length = 255)
    private String content;

    @Column(name = "post_image_url", length = 500)
    @Setter
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 예: 'MOVIE', 'DRAMA'
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(name = "content_id", nullable = false)
    private Long contentId;
}


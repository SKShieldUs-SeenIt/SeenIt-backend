package com.basic.miniPjt5.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Post extends BaseEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_code", unique = true, nullable = false, length = 15)
    private String code;

    @Column(name = "post_title", nullable = false, length = 100)
    private String title;

    @Column(name = "post_body", nullable = false, length = 255)
    private String body;

    @Column(name = "post_image_url")
    private String imageUrl;

    //'MOVIE', 'DRAMA'
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", length = 10, nullable = false)
    private String contentType;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}

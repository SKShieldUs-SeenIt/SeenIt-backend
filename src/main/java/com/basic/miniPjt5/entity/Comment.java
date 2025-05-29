package com.basic.miniPjt5.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "comment_content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Setter
    @JsonBackReference//무한 순환 참조를 방지하기 위해 사용
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference //무한 순환 참조를 방지하기 위해 사용
    private List<Comment> childComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    //대댓글 없을 때
    public Comment(String content, User user, Post post){
        this.content = content;
        this.user = user;
        this.post = post;
        this.parentComment = null;
    }
    //대댓글 있을 때
    public Comment(String content, User user, Post post, Comment parentComment)
    {
        this.content = content;
        this.user = user;
        this.post = post;
        this.parentComment = parentComment;
    }

    public void addChildComment(Comment childComment){
        this.childComments.add(childComment);
        childComment.setParentComment(this);
    }

}

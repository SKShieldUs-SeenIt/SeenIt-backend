package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findCommentsByPost(@Param("postId") Long postId);

    Optional<Comment> findCommentById(@Param("id") Long id);
}

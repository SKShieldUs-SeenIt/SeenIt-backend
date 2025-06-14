package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p.code FROM Post p WHERE p.code LIKE :prefix% ORDER BY p.code DESC LIMIT 1")
    Optional<String> findLastPostCodeByPrefix(@Param("prefix") String prefix);

    @Query("SELECT p From Post p WHERE p.contentType = :contentType AND p.contentId = :contentId")
    List<Post> findPostsByContent(@Param("contentType") ContentType contentType, @Param("contentId") Long contentId);

    Optional<Post> findByCode(String code);

    boolean existsByCode(String code);

    void deleteByContentTypeAndContentId(ContentType contentType, Long contentId);

}

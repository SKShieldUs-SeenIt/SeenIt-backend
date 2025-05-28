package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("Select p.code FROM Post p WHERE p.code LIKE :prefix% ORDER BY p.code DESC LIMIT 1")
    Optional<String> findLastPostCodeByPrefix(@Param("prefix") String prefix);

    Optional<Post> findByCode(String code);

    boolean existsByCode(String code);
}

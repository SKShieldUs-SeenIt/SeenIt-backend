package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.User;

import com.basic.miniPjt5.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 카카오 ID 존재 여부 확인
    boolean existsByKakaoId(String kakaoId);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 활성 상태 사용자 조회
    Optional<User> findByKakaoIdAndStatus(String kakaoId, UserStatus status);
  
  
    Optional<User> findByKakaoId(String kakaoId);
}
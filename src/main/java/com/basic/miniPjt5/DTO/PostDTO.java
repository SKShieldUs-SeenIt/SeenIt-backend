package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createRequest {
        @NotBlank(message = "제목은 필수 입력사항 입니다.")
        @Size(max = 100, message = "제목은 100자 이상 작성할 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String body;

        private MultipartFile image;

        private String contentType;

        private Long contentId;

        private User user;

        public Post toEntity(String imageUrl, User user) {
            return Post.builder()
                    .title(title)
                    .body(body)
                    .imageUrl(imageUrl)
                    .contentType(contentType)
                    .contentId(contentId)
                    .user(user)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateRequest {
        @NotBlank(message = "제목은 필수 입력사항 입니다.")
        @Size(max = 100, message = "제목은 100자 이상 작성할 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String body;

        private MultipartFile image;

        private boolean deleteExistingImage;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private Long id;
        private String code;
        private String title;
        private String body;
        private String imageUrl;
        private String contentType;
        private Long contentId;
        private UserDTO.SimpleResponse user;

        public static ListResponse fromEntity(Post post) {
            return ListResponse.builder()
                    .id(post.getId())
                    .code(post.getCode())
                    .title(post.getTitle())
                    .body(post.getBody())
                    .contentType(post.getContentType())
                    .contentId(post.getContentId())
                    .user(UserDTO.SimpleResponse.fromEntity(post.getUser()))
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String code;
        private String title;
        private String body;
        private String imageUrl;
        private String contentType;
        private Long contentId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserDTO.SimpleResponse user;

        public static Response fromEntity(Post post) {
            return Response.builder()
                    .id(post.getId())
                    .code(post.getCode())
                    .title(post.getTitle())
                    .body(post.getBody())
                    .contentType(post.getContentType())
                    .contentId(post.getContentId())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .user(UserDTO.SimpleResponse.fromEntity(post.getUser()))
                    .build();
        }
    }
}

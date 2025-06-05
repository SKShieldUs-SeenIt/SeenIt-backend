package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class PostDTO {
    @Schema(description = "게시글 생성 DTO", name = "PostCreateRequest")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createRequest {
        @Schema(description = "게시글 제목")
        @NotBlank(message = "제목은 필수 입력사항 입니다.")
        @Size(max = 100, message = "제목은 100자 이상 작성할 수 없습니다.")
        private String title;

        @Schema(description = "게시글 내용")
        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String body;

        @Schema(description = "게시글 이미지")
        private MultipartFile image;

        @Schema(description = "연결된 컨텐츠 종류", allowableValues = {"MOVIE", "DRAMA"})
        private ContentType contentType;

        @Schema(description = "연결된 컨텐츠의 Id값")
        private Long contentId;

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

    @Schema(description = "게시글 수정 DTO", name = "PostUpdateRequest")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateRequest {
        @Schema(description = "게시글 제목")
        @NotBlank(message = "제목은 필수 입력사항 입니다.")
        @Size(max = 100, message = "제목은 100자 이상 작성할 수 없습니다.")
        private String title;

        @Schema(description = "게시글 내용")
        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String body;

        @Schema(description = "게시글 이미지")
        private MultipartFile image;

        private boolean deleteExistingImage;

    }

    @Schema(description = "게시글 목록 Response DTO", name = "PostListResponse")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        @Schema(description = "게시글 Id")
        private Long id;

        @Schema(description = "게시글 코드값", example = "'P'+yymmdd+순서")
        private String code;

        @Schema(description = "게시글 제목")
        private String title;

        @Schema(description = "게시글 내용")
        private String body;

        @Schema(description = "게시글 이미지 주소")
        private String imageUrl;

        @Schema(description = "연결된 컨텐츠 종류", allowableValues = {"MOVIE", "DRAMA"})
        private ContentType contentType;

        @Schema(description = "연결된 컨텐츠의 Id값")
        private Long contentId;

        @Schema(description = "작성한 사용자")
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

    @Schema(description = "게시글 단건 Response DTO", name = "PostResponse")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @Schema(description = "게시글 Id")
        private Long id;

        @Schema(description = "게시글 코드값", example = "'P'+yymmdd+순서")
        private String code;

        @Schema(description = "게시글 제목")
        private String title;

        @Schema(description = "게시글 내용")
        private String body;

        @Schema(description = "게시글 이미지 주소")
        private String imageUrl;

        @Schema(description = "연결된 컨텐츠 종류", allowableValues = {"MOVIE", "DRAMA"})
        private ContentType contentType;

        @Schema(description = "연결된 컨텐츠의 Id값")
        private Long contentId;

        @Schema(description = "생성 시간")
        private LocalDateTime createdAt;

        @Schema(description = "수정 시간")
        private LocalDateTime updatedAt;

        @Schema(description = "작성한 사용자")
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

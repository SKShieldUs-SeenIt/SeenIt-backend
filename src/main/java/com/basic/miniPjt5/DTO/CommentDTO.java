package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.Comment;
import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO {
    @Schema(description = "댓글 생성 DTO", name = "CommentCreateRequest")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createRequest {
        @Schema(description = "댓글 내용")
        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String content;

        @Schema(description = "댓글 부모 Id(댓글, 대댓글 확인용)")
        private Long parentId;

        public Comment toEntity(User user, Post post) {
            return Comment.builder()
                    .content(content)
                    .user(user)
                    .post(post)
                    .build();
        }
    }

    @Schema(description = "댓글 수정 DTO", name = "CommentUpdateRequest")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateRequest {
        @Schema(description = "댓글 내용")
        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String content;
    }

    @Schema(description = "댓글 Response DTO", name = "CommentResponse")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @Schema(description = "댓글 고유 Id")
        private Long id;

        @Schema(description = "댓글 내용")
        private String content;

        @Schema(description = "댓글 부모 Id(댓글, 대댓글 확인용)")
        private Long parentCommentId;

        @Schema(description = "대댓글 List")
        private List<Response> childComments;

        @Schema(description = "생성 시간")
        private LocalDateTime createdAt;

        @Schema(description = "수정 시간")
        private LocalDateTime updatedAt;

        @Schema(description = "작성한 사용자")
        private UserDTO.SimpleResponse user;

        @Schema(description = "연결된 게시글 Id")
        private Long postId;

        public static CommentDTO.Response fromEntity(Comment comment) {
            //null이면(댓글일 때) true, not null이면(대댓글일 때) false
            boolean isParent = comment.getParentComment() == null;

            // 1. parentCommentId 처리
            Long parentCommentId = isParent ? null : comment.getParentComment().getId();

            // 2. childComments 처리 로직
            List<Response> childComments = Collections.emptyList();

            if (isParent) {
                if (comment.getChildComments() != null) { //대댓글이 있는 경우
                    childComments = comment.getChildComments().stream()
                            .map(Response::fromEntity)
                            .collect(Collectors.toList());
                }
            }

            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .parentCommentId(parentCommentId)
                    .childComments(childComments)
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .user(UserDTO.SimpleResponse.fromEntity(comment.getUser()))
                    .postId(comment.getPost().getId())
                    .build();
        }
    }
}

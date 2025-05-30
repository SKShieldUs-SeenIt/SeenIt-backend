package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.Comment;
import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.entity.User;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createRequest {
        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String content;

        private Long parentId;

        public Comment toEntity(User user, Post post) {
            return Comment.builder()
                    .content(content)
                    .user(user)
                    .post(post)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateRequest {
        @NotBlank(message = "내용은 필수 입력사항 입니다.")
        @Size(max = 255, message = "내용은 255자 이상 작성할 수 없습니다.")
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String content;
        private Long parentCommentId;
        private List<Response> childComments;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserDTO.SimpleResponse user;
        private Long postId;

        public static CommentDTO.Response fromEntity(Comment comment) {
            //null이면(댓글일 때) true, not null이면(대댓글일 때) false
            boolean isParent = comment.getParentComment() == null;

            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .parentCommentId(isParent ? null : comment.getParentComment().getId())
                    .childComments(isParent ? comment.getChildComments().stream()
                                    .map(Response::fromEntity)
                                    .collect(Collectors.toList())
                                    :Collections.emptyList())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .user(UserDTO.SimpleResponse.fromEntity(comment.getUser()))
                    .postId(comment.getPost().getId())
                    .build();
        }
    }
}

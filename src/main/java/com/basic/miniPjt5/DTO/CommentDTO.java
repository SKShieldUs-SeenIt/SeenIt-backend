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

        private Long postId;

        private Long userId;

        private User user;

        private Post post;

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
    public static class ListResponse {

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ParentResponse {
            private Long id;
            private String content;
            private Long parentCommentId;
            private List<ChildResponse> childComments;
            private UserDTO.SimpleResponse user;
            private Long postId;

            public static List<ParentResponse> fromEntity(List<Comment> comments) {
                if (comments == null)
                    return Collections.emptyList();
                return comments.stream()
                        .filter(c-> c.getParentComment() == null)
                        .map(ParentResponse::fromEntity)
                        .collect(Collectors.toList());
            }
            public static ParentResponse fromEntity(Comment comment) {
                return ParentResponse.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .parentCommentId(null)
                        .childComments(comment.getChildComments() == null ? Collections.emptyList():
                                        comment.getChildComments().stream()
                                                .map(ChildResponse::fromEntity)
                                                .collect(Collectors.toList()))
                        .user(UserDTO.SimpleResponse.fromEntity(comment.getUser()))
                        .postId(comment.getPost().getId())
                        .build();
            }
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ChildResponse {
            private Long id;
            private String content;
            private Long parentCommentId;
            private UserDTO.SimpleResponse user;
            private Long postId;

            public static ChildResponse fromEntity(Comment comment) {
                return ChildResponse.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .parentCommentId(comment.getParentComment().getId())
                        .user(UserDTO.SimpleResponse.fromEntity(comment.getUser()))
                        .postId(comment.getPost().getId())
                        .build();
            }
        }



    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String content;
        private CommentDTO.Response parentComment;
        private List<ListResponse.ChildResponse> childComments;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserDTO.SimpleResponse user;
        private Long postId;

        public static CommentDTO.Response fromEntity(Comment comment) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .parentComment(Response.fromEntity(comment.getParentComment())
                    .childComments(comment.getChildComments())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .user(UserDTO.SimpleResponse.fromEntity(comment.getUser()))
                    .build();
        }
    }
}

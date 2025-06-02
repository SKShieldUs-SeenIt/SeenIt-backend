package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.CommentDTO;
import com.basic.miniPjt5.entity.Comment;
import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.repository.CommentRepository;
import com.basic.miniPjt5.repository.PostRepository;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private  final UserRepository userRepository;

    public List<CommentDTO.Response> getCommentsByPost(String postCode){
        Post post = postRepository.findByCode(postCode)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));

        return commentRepository.findCommentsByPost(post.getId())
                .stream()
                .map(CommentDTO.Response::fromEntity)
                .toList();
    }

    @Transactional
    public CommentDTO.Response createComment(CommentDTO.createRequest request, Long userId, String postCode){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByCode(postCode)
                .orElseThrow(()->new BusinessException(ErrorCode.POST_NOT_FOUND));

        if(request.getContent() == null)
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "내용을 입력해주세요");

        Comment comment = request.toEntity(user,post);

        //대댓글인 경우
        if(request.getParentId() != null){
            Comment parent = commentRepository.findCommentById(request.getParentId())
                    .orElseThrow(()->new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
            comment.setParentComment(parent);
        }

        Comment createComment = commentRepository.save(comment);
        return CommentDTO.Response.fromEntity(createComment);
    }

    @Transactional
    public CommentDTO.Response updateComment(CommentDTO.updateRequest request, Long userId, Long commentId){
        Comment comment = commentRepository.findCommentById(commentId)
                .orElseThrow(()->new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.COMMENT_ACCESS_DENIED, "댓글의 작성자만 수정할 수 있습니다.");
        }
        if(request.getContent() == null)
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "내용을 입력해주세요");
        comment.setContent(request.getContent());

        Comment updateComment = commentRepository.save(comment);

        return CommentDTO.Response.fromEntity(updateComment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findCommentById(id)
                .orElseThrow(()->new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
    }
}

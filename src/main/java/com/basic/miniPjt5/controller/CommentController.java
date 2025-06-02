package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.CommentDTO;
import com.basic.miniPjt5.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/posts/{postCode}/comments")
    public ResponseEntity<List<CommentDTO.Response>> getCommentsByPost(@PathVariable String postCode) {
        List<CommentDTO.Response> comments = commentService.getCommentsByPost(postCode);
        return ResponseEntity.ok(comments);
    }

//    @PostMapping("/posts/{postCode}/comments")
//    public ResponseEntity<CommentDTO.Response> createComment(@Valid @ModelAttribute CommentDTO.createRequest request,
//                                                             @AuthenticationPrincipal UserPrincipal userPrincipal,
//                                                             @PathVariable String postCode) {
//        CommentDTO.Response createComment= commentService.createComment(request, userPrincipal.getId(), postCode);
//        return ResponseEntity.ok(createComment);
//    }

    @PostMapping("/posts/{postCode}/comments")
    public ResponseEntity<CommentDTO.Response> createComment(@Valid @ModelAttribute CommentDTO.createRequest request,
                                                             @RequestParam("userId") Long userId,
                                                             @PathVariable String postCode) {
        CommentDTO.Response createComment= commentService.createComment(request, userId, postCode);
        return ResponseEntity.ok(createComment);
    }
//    @PutMapping("/comments/{id}")
//    public ResponseEntity<CommentDTO.Response> updateComment(@Valid @ModelAttribute CommentDTO.updateRequest request,
//                                                             @AuthenticationPrincipal UserPrincipal userPrincipal,
//                                                             @PathVariable Long id) {
//        CommentDTO.Response updateComment = commentService.updateComment(request, userPrincipal.getId(), id);
//        return ResponseEntity.ok(updateComment);
//    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDTO.Response> updateComment(@Valid @ModelAttribute CommentDTO.updateRequest request,
                                                             @RequestParam("userId") Long userId,
                                                             @PathVariable Long id) {
        CommentDTO.Response updateComment = commentService.updateComment(request, userId, id);
        return ResponseEntity.ok(updateComment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id){
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

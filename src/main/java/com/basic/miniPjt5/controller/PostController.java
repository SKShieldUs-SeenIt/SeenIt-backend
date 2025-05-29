package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.controller.dto.PostDTO;
import com.basic.miniPjt5.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO.ListResponse>> getAllPost(){
        List<PostDTO.ListResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.Response> getPostById(@PathVariable Long id){
        PostDTO.Response post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/detail/{code}")
    public ResponseEntity<PostDTO.Response> getPostByCode(@PathVariable String code){
        PostDTO.Response post = postService.getPostByCode(code);
        return ResponseEntity.ok(post);
    }

    @PostMapping(value = "/create/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO.Response> createPost(@Valid @ModelAttribute PostDTO.createRequest request,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal){
        PostDTO.Response createPost = postService.createPost(request, userPrincipal.getId());
        return ResponseEntity.ok(createPost);
    }

    @PutMapping(value = "/update/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO.Response> updatePost(@PathVariable String code,
                                                        @Valid @ModelAttribute PostDTO.updateRequest request,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal){
        PostDTO.Response updatedPost = postService.updatePost(code, request, userPrincipal.getId());
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deletePost(@PathVariable String code){
        postService.deletePost(code);
        return ResponseEntity.noContent().build();
    }
}

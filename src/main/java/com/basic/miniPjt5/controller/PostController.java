package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.ContentSimpleDTO;
import com.basic.miniPjt5.DTO.PostDTO;
import com.basic.miniPjt5.enums.ContentType;
import com.basic.miniPjt5.service.PostService;
import com.basic.miniPjt5.service.TMDBDataInitializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final TMDBDataInitializationService tmdbDataInitializationService;
    //전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<PostDTO.ListResponse>> getAllPost(){
        List<PostDTO.ListResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // /api/posts/content?type=MOVIE&id=1 요청
    //content 한개당 관련 게시글 조회
    @GetMapping("/content")
    public ResponseEntity<List<PostDTO.ListResponse>> getPostsByContent(@RequestParam("type") String contentType,
                                                                        @RequestParam("id") Long contentId){
        List<PostDTO.ListResponse> posts = postService.getPostsByContent(contentType, contentId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{code}")
    public ResponseEntity<PostDTO.Response> getPostByCode(@PathVariable String code){
        PostDTO.Response post = postService.getPostByCode(code);
        return ResponseEntity.ok(post);
    }

//    //input type hidden으로 contentType, contentId
//    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<PostDTO.Response> createPost(@Valid @ModelAttribute PostDTO.createRequest request,
//                                                       @AuthenticationPrincipal UserPrincipal userPrincipal){
//        PostDTO.Response createPost = postService.createPost(request, userPrincipal.getId());
//        return ResponseEntity.ok(createPost);
//    }

//    input type hidden으로 contentType, contentId
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO.Response> createPost(@Valid @ModelAttribute PostDTO.createRequest request,
                                                       @RequestParam("userId")Long userId){
        PostDTO.Response createPost = postService.createPost(request, userId);
        return ResponseEntity.ok(createPost);
    }

//    @PutMapping(value = "/update/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<PostDTO.Response> updatePost(@PathVariable String code,
//                                                        @Valid @ModelAttribute PostDTO.updateRequest request,
//                                                       @AuthenticationPrincipal UserPrincipal userPrincipal){
//        PostDTO.Response updatedPost = postService.updatePost(code, request, userPrincipal.getId());
//        return ResponseEntity.ok(updatedPost);
//    }

    @PutMapping(value = "/update/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO.Response> updatePost(@PathVariable String code,
                                                       @Valid @ModelAttribute PostDTO.updateRequest request,
                                                       @RequestParam("userId")Long userId){
        PostDTO.Response updatedPost = postService.updatePost(code, request, userId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deletePost(@PathVariable String code){
        postService.deletePost(code);
        return ResponseEntity.noContent().build();
    }

    //MOVIE or DRAMA 관련 필요한 정보 조회
    @GetMapping("/content/{contentType}/{contentId}")
    public ResponseEntity<ContentSimpleDTO> getContentSummary(@PathVariable ContentType contentType, @PathVariable Long contentId) {
        ContentSimpleDTO dto = tmdbDataInitializationService.getContentSummary(contentType, contentId);
        return ResponseEntity.ok(dto);
    }

}

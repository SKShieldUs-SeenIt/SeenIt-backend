package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.ContentSimpleDTO;
import com.basic.miniPjt5.DTO.PostDTO;
import com.basic.miniPjt5.enums.ContentType;
import com.basic.miniPjt5.exception.advice.ErrorResponse;
import com.basic.miniPjt5.security.UserPrincipal;
import com.basic.miniPjt5.service.PostService;
import com.basic.miniPjt5.service.TMDBDataInitializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시글 API", description = "게시글 생성, 조회, 수정, 삭제, 현재 연결된 content(MOVIE or DRAMA) 일부 정보 조회 관련 API")
public class PostController {
    private final PostService postService;
    private final TMDBDataInitializationService tmdbDataInitializationService;

    @Operation(summary = "전체 게시글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공", content = @Content(schema = @Schema(implementation = PostDTO.ListResponse.class)))})
    @GetMapping
    public ResponseEntity<List<PostDTO.ListResponse>> getAllPost(){
        List<PostDTO.ListResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // /api/posts/content?type=MOVIE&id=1 요청
    //content 한개당 관련 게시글 조회
    @Operation(summary = "콘텐츠 별 게시글 조회", description = "type과 id를 통한 콘텐츠 별 게시글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostDTO.ListResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.(유효하지 않은 콘텐츠 타입)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청된 콘텐츠(영화/드라마)를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/content")
    public ResponseEntity<List<PostDTO.ListResponse>> getPostsByContent(@Parameter(name = "type", description = "콘텐츠 타입 (MOVIE 또는 DRAMA)", in = ParameterIn.QUERY, required = true,
                                                                                    schema = @Schema(type = "String", allowableValues = {"MOVIE", "DRAMA"}, example = "MOVIE"))
                                                                        @RequestParam("type") ContentType contentType,
                                                                        @Parameter(name = "id", description = "콘텐츠 ID (MOVIE ID 또는 DRAMA ID)", in = ParameterIn.QUERY, required = true,
                                                                                schema = @Schema(type = "integer", example = "1"))
                                                                        @RequestParam("id") Long contentId){
        List<PostDTO.ListResponse> posts = postService.getPostsByContent(contentType, contentId);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 단건 조회", description = "코드를 통한 게시글 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostDTO.Response.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.(잘못된 형식의 코드 또는 존재하지 않는 게시글)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{code}")
    public ResponseEntity<PostDTO.Response> getPostByCode(@Parameter(name = "code", description = "조회할 게시글의 고유 코드", in = ParameterIn.PATH, required = true,
                                                                    schema = @Schema(type = "string", example = "P25052800001"))
                                                          @PathVariable String code){
        PostDTO.Response post = postService.getPostByCode(code);
        return ResponseEntity.ok(post);
    }

//    input type hidden으로 contentType, contentId
@Operation(summary = "게시글 생성", description = "게시글 생성(제목, 내용, 이미지, 콘텐츠 정보(타입, ID 값), 사용자 ID가 필요합니다.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 생성 성공",
                content = @Content(schema = @Schema(implementation = PostDTO.Response.class))),
        @ApiResponse(responseCode = "400", description = "게시글 생성 실패 (잘못된 데이터, 필수 필드 누락 등)",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (유효한 JWT 토큰 필요)",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 (예: 이미지 저장 실패)",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO.Response> createPost(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                description = "게시글 생성 요청 데이터",
                                                                required = true,
                                                                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                                                        schema = @Schema(implementation = PostDTO.createRequest.class))
                                                        )
                                                       @Valid @ModelAttribute PostDTO.createRequest request,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal){
        PostDTO.Response createPost = postService.createPost(request, userPrincipal.getId());
        return ResponseEntity.ok(createPost);
    }

    @Operation(summary = "게시글 수정", description = "게시글 수정(코드, 제목, 내용, 이미지, 사용자 ID가 필요합니다. 이미지는 유지,변경,삭제할 수 있습니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                    content = @Content(schema = @Schema(implementation = PostDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "게시글 수정 실패 (잘못된 데이터, 필수 필드 누락 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (유효한 JWT 토큰 필요)", // 인증 실패 시 401
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자만 수정할 수 있습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 (예: 이미지 저장 실패)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/update/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO.Response> updatePost(@Parameter(name = "code", description = "게시글의 고유 코드", in = ParameterIn.PATH, required = true,
                                                                   schema = @Schema(type = "string", example = "P25052800001"))
                                                           @PathVariable String code,
                                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                   description = "게시글 수정 요청 데이터",
                                                                   required = true,
                                                                   content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                                                           schema = @Schema(implementation = PostDTO.updateRequest.class))
                                                           )
                                                            @Valid @ModelAttribute PostDTO.updateRequest request,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal){
        PostDTO.Response updatedPost = postService.updatePost(code, request, userPrincipal.getId());
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "게시글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (유효한 JWT 토큰 필요)", // 인증 실패 시 401
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자만 삭제할 수 있습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 (예: 이미지 삭제 실패)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deletePost(@Parameter(name = "code", description = "게시글의 고유 코드", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "string", example = "P25052800001"))
                                            @PathVariable String code,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal){
        postService.deletePost(code, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

    //MOVIE or DRAMA 관련 필요한 정보 조회
    @Operation(summary = "연결된 콘텐츠 일부 정보 조회", description = "콘텐츠 정보(타입, ID)를 통해 해당 콘텐츠의 일부 정보를 조회(Id, Title, Poster path) ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "콘텐츠 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = ContentSimpleDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. (유효하지 않은 ContentType)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "콘텐츠를 찾을 수 없습니다.(유효하지 않은 ContentId)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/content/{contentType}/{contentId}")
    public ResponseEntity<ContentSimpleDTO> getContentSummary( @Parameter(name = "contentType", description = "콘텐츠 타입(MOVIE, DRAMA)", in = ParameterIn.PATH, required = true,
                                                                schema = @Schema(type = "string", allowableValues = {"MOVIE", "DRAMA"}, example = "MOVIE"))
                                                                @PathVariable ContentType contentType,
                                                               @Parameter(name = "contentId", description = "콘텐츠의 고유 ID", in = ParameterIn.PATH, required = true,
                                                                       schema = @Schema(type = "integer", format = "int64"))
                                                               @PathVariable Long contentId) {
        ContentSimpleDTO dto = tmdbDataInitializationService.getContentSummary(contentType, contentId);
        return ResponseEntity.ok(dto);
    }

}

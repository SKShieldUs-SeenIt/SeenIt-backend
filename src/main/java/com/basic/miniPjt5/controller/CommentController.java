package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.CommentDTO;
import com.basic.miniPjt5.exception.advice.ErrorResponse;
import com.basic.miniPjt5.security.UserPrincipal;
import com.basic.miniPjt5.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "댓글 API", description = "댓글 생성, 조회, 수정, 삭제 관련 API")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "게시글 별 댓글들 조회", description = "postCode를 이용해서 게시글 별 댓글들 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CommentDTO.Response.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.(code 오류)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/posts/{postCode}/comments")
    public ResponseEntity<List<CommentDTO.Response>> getCommentsByPost(@Parameter(name = "postCode", description = "조회할 게시글의 고유 코드", in = ParameterIn.PATH, required = true,
                                                                        schema = @Schema(type = "string", example = "P25052800001"))
                                                                        @PathVariable String postCode) {
        List<CommentDTO.Response> comments = commentService.getCommentsByPost(postCode);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 생성", description = "댓글 생성(내용, 댓글 부모 Id)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공",
                    content = @Content(schema = @Schema(implementation = CommentDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "댓글 생성 실패 (잘못된 데이터, 필수 필드 누락 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (유효한 JWT 토큰 필요)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없거나(code) 댓글을 찾을 수 없습니다.(부모 댓글)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/posts/{postCode}/comments")
    public ResponseEntity<CommentDTO.Response> createComment(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 생성 요청 데이터",
                                                                         required = true,
                                                                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                                 schema = @Schema(implementation = CommentDTO.createRequest.class))
                                                                 )
                                                                @Valid @RequestBody CommentDTO.createRequest request,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @Parameter(name = "postCode", description = "조회할 게시글의 고유 코드", in = ParameterIn.PATH, required = true,
                                                                     schema = @Schema(type = "string", example = "P25052800001"))
                                                             @PathVariable String postCode) {
        CommentDTO.Response createComment= commentService.createComment(request, userPrincipal.getId(), postCode);
        return ResponseEntity.ok(createComment);
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정(내용, 댓글 고유Id, 사용자 Id)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(schema = @Schema(implementation = CommentDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "댓글 수정 실패 (잘못된 데이터, 필수 필드 누락 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (유효한 JWT 토큰 필요)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자만 수정할 수 있습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDTO.Response> updateComment(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 수정 요청 데이터",
                                                                         required = true,
                                                                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                                 schema = @Schema(implementation = CommentDTO.createRequest.class))
                                                                 )
                                                                @Valid @RequestBody CommentDTO.updateRequest request,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @Parameter(name = "id", description = "댓글의 고유 ID", in = ParameterIn.PATH, required = true,
                                                                     schema = @Schema(type = "integer", format = "int64"))
                                                             @PathVariable Long id) {
        CommentDTO.Response updateComment = commentService.updateComment(request, userPrincipal.getId(), id);
        return ResponseEntity.ok(updateComment);
    }

    @Operation(summary = "댓글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (유효한 JWT 토큰 필요)", // 인증 실패 시 401
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자만 삭제할 수 있습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@Parameter(name = "id", description = "댓글의 고유 ID", in = ParameterIn.PATH, required = true,
                                                schema = @Schema(type = "integer", format = "int64"))
                                                @PathVariable Long id,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal){
        commentService.deleteComment(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}

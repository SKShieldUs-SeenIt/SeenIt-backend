package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.PostDTO;
import com.basic.miniPjt5.repository.UserRepository;
import com.basic.miniPjt5.entity.Comment;
import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.repository.CommentRepository;
import com.basic.miniPjt5.repository.PostRepository;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Value("%{file.upload-dir}")
    private String uploadDir;

    public List<PostDTO.ListResponse> getAllPosts(){
        return postRepository.findAll()
                .stream()
                .map(PostDTO.ListResponse::fromEntity)
                .toList();
    }

    public List<PostDTO.ListResponse> getPostsByContent(String contentType, Long contentId){
        return postRepository.findPostsByContent(contentType, contentId)
                .stream()
                .map(PostDTO.ListResponse::fromEntity)
                .toList();
    }

    public PostDTO.Response getPostById(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(()->new BusinessException(ErrorCode.POST_NOT_FOUND));
        return PostDTO.Response.fromEntity(post);
    }

    public PostDTO.Response getPostByCode(String code){
        Post post = postRepository.findByCode(code)
                .orElseThrow(()->new BusinessException(ErrorCode.POST_NOT_FOUND));
        return PostDTO.Response.fromEntity(post);
    }

    @Transactional
    public PostDTO.Response createPost(PostDTO.createRequest request, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(request.getTitle() == null)
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "제목을 입력해주세요");

        if(request.getBody() == null)
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "내용을 입력해주세요");

        String postCode = generateCode();
        if (postRepository.existsByCode(postCode))
            throw new BusinessException(ErrorCode.POST_CODE_DUPLICATE);

        String imageUrl = saveImage(request.getImage());

        Post post = request.toEntity(imageUrl, user);
        post.setCode(postCode);

        Post createPost = postRepository.save(post);
        return PostDTO.Response.fromEntity(createPost);
    }

    @Transactional
    public PostDTO.Response updatePost(String code, PostDTO.updateRequest request, Long userId){
        Post post = postRepository.findByCode(code)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.POST_ACCESS_DENIED, "게시글의 작성자만 수정할 수 있습니다.");
        }

        if(request.getTitle() == null)
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "제목을 입력해주세요");

        if(request.getBody() == null)
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "내용을 입력해주세요");

        String newImageUrl = null;

        //이미지 삭제요청일 경우
        if(request.isDeleteExistingImage()){
            deleteImage(post.getImageUrl());
            newImageUrl = null;
        }
        //기존 이미지 삭제 후 새로운 이미지 업로드일 경우
        else if (request.getImage() != null && !request.getImage().isEmpty()) {
            deleteImage(post.getImageUrl());
            newImageUrl = saveImage(request.getImage());
        }
        //기존 이미지 유지인 경우
        else {
            newImageUrl = post.getImageUrl();
        }

        post.setTitle(request.getTitle());
        post.setBody(request.getBody());
        post.setImageUrl(newImageUrl);

        Post updatedPost = postRepository.save(post);
        return PostDTO.Response.fromEntity(updatedPost);
    }

    @Transactional
    public void deletePost(String code) {
        Post post = postRepository.findByCode(code)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(uploadDir);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "업로드 디렉토리 생성 실패", e);
        }

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path targetPath = Paths.get(uploadDir, fileName);

        try {
            Files.write(targetPath, image.getBytes());
            return "/images/" + fileName;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "이미지 파일 저장 실패", e);
        }
    }

    private void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        String fileName = imageUrl.replace("/images/", "");
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
             throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, "이미지 파일 삭제 실패" + filePath.toString() +", 오류: " + e.getMessage(), e);
        }
    }

    //code는 P25052800001, P25052800002..으로 생성
    private synchronized String generateCode(){
        LocalDate d = LocalDate.now();
        String cPrefix = "P"+ d.format(DateTimeFormatter.ofPattern("yyMMdd"));

        Optional<String> lastCode = postRepository.findLastPostCodeByPrefix(cPrefix);

        int seqNum = 1;
        if(lastCode.isPresent()){
            //28001부분만 get
            String seq = lastCode.get().substring(cPrefix.length());
            try{
                seqNum = Integer.parseInt(seq)+1;
            }
            catch (NumberFormatException e) {
                System.err.println("post_code 시퀀스 파싱 오류: " + lastCode + ", 기본값 1로 시작합니다.");
                seqNum = 1;
            }
        }
        final int length = 5;  //하루에 00001~99999까지 가능
        String newSeq = String.format("%0"+length+"d", seqNum);
        return cPrefix + newSeq;
    }
}

package com.basic.miniPjt5.service;

import com.basic.miniPjt5.controller.dto.PostDTO;
import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.EntityNotFoundException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    public List<PostDTO.ListResponse> getAllPosts(){
        return postRepository.findAll()
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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 2. Post 엔티티를 생성합니다.
        // 이때 User 엔티티 객체를 Post에 설정하여 연관관계를 맺습니다.
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .contentType(request.getContentType())
                .contentId(request.getContentId())
                .user(user)
                .build();

        String postCode = generateCode();
        post.setCode(postCode);

        Post createPost = postRepository.save(post);
        return PostDTO.Response.fromEntity(createPost);
    }

    @Transactional
    public PostDTO.Response updatePost(String code, PostDTO.updateRequest request){
        Post post = postRepository.findByCode(code)
                .orElseThrow(()-> new EntityNotFoundException("Post",code));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());

        Post updatedPost = postRepository.save(post);
        return PostDTO.Response.fromEntity(updatedPost);
    }

    @Transactional
    public void deletePost(String code) {
        Post post = postRepository.findByCode(code)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
    }


    private synchronized String generateCode(){
        //code는 P25052800001, P25052800002..으로 생성
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

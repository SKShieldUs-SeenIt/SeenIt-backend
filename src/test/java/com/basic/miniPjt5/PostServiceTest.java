package com.basic.miniPjt5;

import com.basic.miniPjt5.DTO.PostDTO;
import com.basic.miniPjt5.entity.Post;
import com.basic.miniPjt5.repository.PostRepository;
import com.basic.miniPjt5.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test2")
@Transactional
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @Test
    @Rollback(value = false) //Rollback 처리하지 마세요!!
    void postSaveTest() {
        PostDTO.createRequest request = PostDTO.createRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .imageUrl("http://test.com/image.jpg")
                .build();

        Long userId = 1L;

        // when
        PostDTO.Response saved = postService.createPost(request, userId);

        // then
        Post post = postRepository.findById(saved.getId()).orElse(null);
        assertThat(post).isNotNull();
        assertThat(post.getTitle()).isEqualTo("테스트 제목");
    }
}

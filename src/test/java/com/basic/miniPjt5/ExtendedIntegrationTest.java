package com.basic.miniPjt5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

// 통합 테스트 확장
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "tmdb.api.max-pages-per-request=1",
        "tmdb.api.api-delay-ms=0"
})
class ExtendedIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
    }

    @Test
    @DisplayName("API 응답 시간 테스트")
    void testApiResponseTime() {
        // when
        long startTime = System.currentTimeMillis();
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/health", Map.class);
        long endTime = System.currentTimeMillis();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(endTime - startTime).isLessThan(5000); // 5초 이내

        System.out.println("API 응답 시간: " + (endTime - startTime) + "ms");
    }

    @Test
    @DisplayName("동시 요청 처리 테스트")
    void testConcurrentRequests() throws InterruptedException {
        // given
        int numberOfRequests = 10;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        List<ResponseEntity<Map>> responses = Collections.synchronizedList(new ArrayList<>());

        // when
        for (int i = 0; i < numberOfRequests; i++) {
            new Thread(() -> {
                try {
                    ResponseEntity<Map> response = restTemplate.getForEntity(
                            baseUrl + "/health", Map.class);
                    responses.add(response);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await(30, TimeUnit.SECONDS);

        // then
        assertThat(responses).hasSize(numberOfRequests);
        responses.forEach(response -> 
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK));

        System.out.println("동시 요청 처리 완료: " + responses.size() + "개");
    }
}
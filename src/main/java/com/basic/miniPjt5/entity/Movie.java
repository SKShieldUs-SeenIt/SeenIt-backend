package com.basic.miniPjt5.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "movies")
@Builder // 빌더 패턴 사용을 위해 추가
public class Movie implements Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id") // 컬럼명 명시 (선택 사항)
    private Long id; // TMDB ID와 다를 수 있으므로 내부 ID로 사용

    @Column(unique = true, nullable = false) // TMDB ID는 고유해야 함
    private Long tmdbId; // TMDB에서 제공하는 고유 ID

    @Column(nullable = false)
    private String title; // 영화 제목

    @ManyToMany // Movie와 Genre는 다대다 관계
    @JoinTable(
        name = "movie_genre", // 조인 테이블 이름
        joinColumns = @JoinColumn(name = "movie_id"), // Movie 엔티티의 PK (movie_id)
        inverseJoinColumns = @JoinColumn(name = "genre_id") // Genre 엔티티의 PK (genre_id)
    )
    @JsonIgnore
    @Builder.Default
    private List<Genre> genres = new ArrayList<>(); // 영화가 속한 장르 리스트

    @Column(nullable = false)
    private Integer voteCount; // 투표 수

    @Column(nullable = false)
    private Double voteAverage; // 평균 별점

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true) // Movie와 Review는 일대다 관계
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>(); // 이 영화에 대한 리뷰 리스트

    // TMDB API에서 가져올 수 있는 추가 필드들 (선택 사항)
    @Column(length = 2000) // 긴 줄거리 저장을 위해 길이 지정
    private String overview; // 영화 줄거리
    private String posterPath; // 포스터 이미지 경로 (TMDB에서 제공)
    private String releaseDate; // 개봉일 (String으로 저장 권장, 필요시 LocalDate로 파싱)
}
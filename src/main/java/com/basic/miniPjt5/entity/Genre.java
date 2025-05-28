package com.basic.miniPjt5.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "genres")
public class Genre {

    @Id
    // TMDB Genre ID를 그대로 사용
    // @GeneratedValue 제거하고 TMDB ID를 직접 할당
    @Column(name = "genre_id")
    private Long id; // TMDB에서 제공하는 장르 ID

    @Column(nullable = false, unique = true)
    private String name; // 장르 이름 (예: Action, Comedy)

    @ManyToMany(mappedBy = "genres") // Movie 엔티티의 'genres' 필드에 의해 매핑됨
    private List<Movie> movies = new ArrayList<>();

    @ManyToMany(mappedBy = "genres") // Drama 엔티티의 'genres' 필드에 의해 매핑됨
    private List<Drama> dramas = new ArrayList<>();
}
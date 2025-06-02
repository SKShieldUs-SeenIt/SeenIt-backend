package com.basic.miniPjt5.runner;

import com.basic.miniPjt5.entity.*;
import com.basic.miniPjt5.enums.UserStatus;
import com.basic.miniPjt5.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
@Profile("test2")
public class DataInitRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        // 유저 생성
        User user = userRepository.save(User.builder()
                .kakaoId("123456789")
                .name("테스트 유저")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.png")
                .preferredGenres("Action,Comedy")
                .status(UserStatus.ACTIVE)
                .joinDate(LocalDate.now())
                .build());

        // 장르 생성
        Genre action = Genre.builder().id(28L).name("Action").build();
        Genre comedy = Genre.builder().id(35L).name("Comedy").build();
        genreRepository.saveAll(List.of(action, comedy));

        // 영화 생성
        Movie movie = Movie.builder()
                .tmdbId(100L)
                .title("테스트 영화")
                .voteCount(150)
                .voteAverage(8.3)
                .genres(List.of(action, comedy))
                .overview("이것은 테스트 영화입니다.")
                .posterPath("/poster.jpg")
                .releaseDate("2024-05-30")
                .build();
        movieRepository.save(movie);

        // 드라마 생성
        Drama drama = Drama.builder()
                .tmdbId(200L)
                .title("테스트 드라마")
                .voteCount(300)
                .voteAverage(9.1)
                .genres(List.of(action))
                .overview("이것은 테스트 드라마입니다.")
                .posterPath("/drama-poster.jpg")
                .firstAirDate("2024-01-01")
                .lastAirDate("2024-04-01")
                .numberOfSeasons(1)
                .numberOfEpisodes(10)
                .build();
        dramaRepository.save(drama);

        // 리뷰 생성
        Review movieReview = Review.builder()
                .content("영화 리뷰 테스트입니다.")
                .movie(movie)
                .user(user)
                .build();

        Review dramaReview = Review.builder()
                .content("드라마 리뷰 테스트입니다.")
                .drama(drama)
                .user(user)
                .build();

        reviewRepository.saveAll(List.of(movieReview, dramaReview));

        log.info("Data initialization completed successfully");
    }
}

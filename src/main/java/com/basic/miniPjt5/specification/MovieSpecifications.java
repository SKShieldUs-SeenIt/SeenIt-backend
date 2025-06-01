package com.basic.miniPjt5.specification;

import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.entity.Genre;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.List;

public class MovieSpecifications {

    public static Specification<Movie> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")), 
                "%" + title.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Movie> hasGenres(List<Long> genreIds) {
        return (root, query, criteriaBuilder) -> {
            if (genreIds == null || genreIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Movie, Genre> genreJoin = root.join("genres");
            return genreJoin.get("id").in(genreIds);
        };
    }

    public static Specification<Movie> ratingBetween(Double minRating, Double maxRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null && maxRating == null) {
                return criteriaBuilder.conjunction();
            }
            
            if (minRating != null && maxRating != null) {
                return criteriaBuilder.between(root.get("voteAverage"), minRating, maxRating);
            } else if (minRating != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("voteAverage"), minRating);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("voteAverage"), maxRating);
            }
        };
    }

    public static Specification<Movie> releasedInYear(String year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null || year.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("releaseDate"), year + "%");
        };
    }
}
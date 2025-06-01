package com.basic.miniPjt5.specification;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.List;

public class DramaSpecifications {

    public static Specification<Drama> titleContains(String title) {
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

    public static Specification<Drama> hasGenres(List<Long> genreIds) {
        return (root, query, criteriaBuilder) -> {
            if (genreIds == null || genreIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Drama, Genre> genreJoin = root.join("genres");
            return genreJoin.get("id").in(genreIds);
        };
    }

    public static Specification<Drama> ratingBetween(Double minRating, Double maxRating) {
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

    public static Specification<Drama> seasonsBetween(Integer minSeasons, Integer maxSeasons) {
        return (root, query, criteriaBuilder) -> {
            if (minSeasons == null && maxSeasons == null) {
                return criteriaBuilder.conjunction();
            }
            
            if (minSeasons != null && maxSeasons != null) {
                return criteriaBuilder.between(root.get("numberOfSeasons"), minSeasons, maxSeasons);
            } else if (minSeasons != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("numberOfSeasons"), minSeasons);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("numberOfSeasons"), maxSeasons);
            }
        };
    }

    public static Specification<Drama> firstAiredInYear(String year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null || year.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("firstAirDate"), year + "%");
        };
    }
}
package com.basic.miniPjt5.entity;

import java.util.List;

public interface Content {
    Long getId();
    String getTitle();
    List<Genre> getGenres(); // 다대다 매핑될 Genre 리스트
    Integer getVoteCount();
    Double getVoteAverage();
    List<Review> getReviews(); // 일대다 매핑될 Review 리스트
}
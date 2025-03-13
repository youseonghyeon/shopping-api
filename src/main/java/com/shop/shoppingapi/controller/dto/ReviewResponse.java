package com.shop.shoppingapi.controller.dto;

import com.shop.shoppingapi.entity.Review;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewResponse {

    private String content;
    private Integer rating;
    private Long userId;

    public static ReviewResponse from(Review reviews) {
       return ReviewResponse.builder()
               .content(reviews.getContent())
               .rating(reviews.getRating())
               .userId(reviews.getUserId())
               .build();
    }
}

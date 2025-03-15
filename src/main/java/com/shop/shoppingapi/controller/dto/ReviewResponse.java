package com.shop.shoppingapi.controller.dto;

import com.shop.shoppingapi.entity.Review;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {

    private String content;
    private Integer rating;
    private Long userId;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .content(review.getContent())
                .rating(review.getRating())
                .userId(review.getUser().getId())
                .build();
    }
}

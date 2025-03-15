package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.CreateReviewRequest;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class ReviewConverter {
    public static Review toEntity(CreateReviewRequest createReviewRequest, User user, Product product) {
        return Review.builder()
                .content(createReviewRequest.getContent())
                .rating(createReviewRequest.getRating())
                .product(product)
                .user(user)
                .build();
    }
}

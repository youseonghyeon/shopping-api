package com.shop.shoppingapi.entity.converter;

import com.shop.shoppingapi.controller.dto.CreateReviewRequest;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.Review;
import com.shop.shoppingapi.entity.User;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class ReviewConverter {
    public static Review toEntity(CreateReviewRequest createReviewRequest, User user, Product product) {
        return new Review(createReviewRequest.getContent(), createReviewRequest.getRating(), product, user);
    }
}

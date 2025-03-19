package com.shop.shoppingapi.utils;

import com.shop.shoppingapi.controller.dto.CreateReviewRequest;
import com.shop.shoppingapi.entity.*;
import org.springframework.lang.Nullable;

import java.util.Objects;

public class ReviewFixture {
    private static String contentMock = "좋은 상품입니다! 추천해요.";
    private static Integer ratingMock = 5;

    public static CreateReviewRequest toCreateReviewRequest(
            @Nullable String content,
            @Nullable Integer rating
    ) {
        return CreateReviewRequest.builder()
                .content(Objects.isNull(content) ? contentMock : content)
                .rating(Objects.isNull(rating) ? ratingMock : rating)
                .build();
    }

    public static Review toReview(
            @Nullable String content,
            @Nullable Integer rating,
            @Nullable User user,
            @Nullable Product product
    ) {
        content = Objects.isNull(content) ? contentMock : content;
        rating = Objects.isNull(rating) ? ratingMock : rating;
        user = Objects.requireNonNullElseGet(user, UserFixture::toUser);
        product = Objects.requireNonNullElseGet(product, ProductFixture::toProduct);
        OrderItem orderItem = OrderItemFixture.toOrderItem(product);

        CreateReviewRequest createReviewRequest = toCreateReviewRequest(content, rating);

        return ReviewConverter.toEntity(createReviewRequest, user, product, orderItem);
    }
}

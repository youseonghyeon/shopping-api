package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.entity.OrderItem;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.Review;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.utils.OrderItemFixture;
import com.shop.shoppingapi.utils.ProductFixture;
import com.shop.shoppingapi.utils.ReviewFixture;
import com.shop.shoppingapi.utils.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("상품 ID로 리뷰 조회 성공")
    void findByProductId() {
        // given
        User user = super.save(UserFixture.toUser());
        Product product = super.save(ProductFixture.toProduct());
        OrderItem orderItem = super.save(OrderItemFixture.toOrderItem(product));
        Review review = super.save(ReviewFixture.toReview("내용", 4, user, product, orderItem));
        // when
        List<Review> result = reviewRepository.findByProductId(product.getId());
        // then
        assertEquals(1, result.size());
        Review findReview = result.get(0);
        assertAll(
                () -> assertEquals(review.getId(), findReview.getId()),
                () -> assertEquals(review.getContent(), findReview.getContent()),
                () -> assertEquals(review.getRating(), findReview.getRating()),
                () -> assertEquals(review.getUser().getId(), findReview.getUser().getId()),
                () -> assertEquals(review.getProduct().getId(), findReview.getProduct().getId())
        );

    }
}

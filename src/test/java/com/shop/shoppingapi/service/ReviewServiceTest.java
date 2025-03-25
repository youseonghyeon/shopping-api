package com.shop.shoppingapi.service;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.controller.dto.CreateReviewRequest;
import com.shop.shoppingapi.entity.*;
import com.shop.shoppingapi.repository.ReviewRepository;
import com.shop.shoppingapi.utils.OrderFixture;
import com.shop.shoppingapi.utils.OrderItemFixture;
import com.shop.shoppingapi.utils.ProductFixture;
import com.shop.shoppingapi.utils.UserFixture;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 생성 성공")
    void create_review_success() {
        // given
        User user = UserFixture.toUser();
        Product product = ProductFixture.toProduct();
        super.save(user);
        super.save(product);
        OrderItem orderItem = super.save(OrderItemFixture.toOrderItem(product));
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .orderItemId(orderItem.getId())
                .productId(product.getId())
                .content("리뷰 내용입니다.")
                .rating(4)
                .build();
        // when
        Long reviewId = reviewService.createReview(createReviewRequest, user.getId());

        // then
        assertNotNull(reviewId);
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Review not found"));
        assertNotNull(findReview);
        assertAll(
                () -> assertEquals(product.getId(), findReview.getProduct().getId()),
                () -> assertEquals("리뷰 내용입니다.", findReview.getContent()),
                () -> assertEquals(4, findReview.getRating())
        );
    }

    @Test
    @DisplayName("리뷰 생성 실패: 상품이 존재하지 않음")
    void create_review_fail_product_not_found() {
        // given
        User user = UserFixture.toUser();
        super.save(user);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .productId(1L)
                .content("리뷰 내용입니다.")
                .rating(4)
                .build();
        // when & then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.createReview(createReviewRequest, user.getId()));
    }

    @Test
    @DisplayName("리뷰 생성 실패: 유저가 존재하지 않음")
    void create_review_fail_user_not_found() {
        // given
        Product product = ProductFixture.toProduct();
        super.save(product);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .content("리뷰 내용입니다.")
                .rating(4)
                .build();
        // when & then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.createReview(createReviewRequest, 1L));
    }

    @Test
    @DisplayName("리뷰 생성 실패: 상품을 구매하지 않은 유저")
    void create_review_fail_user_not_purchased_product() {
        // given
        User user = UserFixture.toUser();
        Product product = ProductFixture.toProduct();
        super.save(user);
        super.save(product);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .content("리뷰 내용입니다.")
                .rating(4)
                .build();
        // when & then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.createReview(createReviewRequest, user.getId()));
    }

    @Test
    @DisplayName("리뷰 생성 실패: 이미 리뷰를 작성한 유저")
    void create_review_fail_user_already_reviewed() {
        // given
        User user = UserFixture.toUser();
        super.save(user);
        Product product = ProductFixture.toProduct();
        super.save(product);
        OrderItem orderItem = OrderItemFixture.toOrderItem(product);
        super.save(orderItem);
        Order order = OrderFixture.toOrder(user, orderItem);
        super.save(order);

        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .orderItemId(orderItem.getId())
                .productId(product.getId())
                .content("리뷰 내용입니다.")
                .rating(4)
                .build();
        reviewService.createReview(createReviewRequest, user.getId());
        // when & then
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(createReviewRequest, user.getId()));
    }

    @Test
    @DisplayName("리뷰 생성 실패: 평점이 0 미만")
    void create_review_fail_rating_less_than_zero() {
        // given
        User user = UserFixture.toUser();
        Product product = ProductFixture.toProduct();
        super.save(user);
        super.save(product);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .content("리뷰 내용입니다.")
                .rating(-1)
                .build();
        // when & then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.createReview(createReviewRequest, user.getId()));
    }

    @Test
    @DisplayName("리뷰 생성 실패: 평점이 5 초과")
    void create_review_fail_rating_greater_than_five() {
        // given
        User user = UserFixture.toUser();
        Product product = ProductFixture.toProduct();
        super.save(user);
        super.save(product);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .content("리뷰 내용입니다.")
                .rating(6)
                .build();
        // when & then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.createReview(createReviewRequest, user.getId()));
    }

    @Test
    @DisplayName("리뷰 생성 실패: 내용이 없음")
    void create_review_fail_content_empty() {
        // given
        User user = UserFixture.toUser();
        Product product = ProductFixture.toProduct();
        super.save(user);
        super.save(product);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .content("")
                .rating(4)
                .build();
        // when & then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.createReview(createReviewRequest, user.getId()));
    }

    @Test
    @DisplayName("리뷰 생성 실패: 평점이 null, 내용이 null")
    void create_review_fail_rating_null_content_null() {
        // given
        User user = UserFixture.toUser();
        Product product = ProductFixture.toProduct();
        super.save(user);
        super.save(product);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .content(null)
                .rating(null)
                .build();
        // when & then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.createReview(createReviewRequest, user.getId()));
    }
}

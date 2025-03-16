package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CreateReviewRequest;
import com.shop.shoppingapi.entity.Review;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<List<Review>>> getReviews(@PathVariable Long productId) {
        List<Review> findReviews = reviewService.findReviewsByProductId(productId);
        return ApiResponse.success(findReviews);
    }

    @PostMapping("/reviews/create")
    public ResponseEntity<ApiResponse<Long>> createReview(@RequestBody CreateReviewRequest review) {
        Long userId = SecurityUtils.getUserId();
        log.info("Review registered - user: {}, review: {}", userId, review);
        Long reviewId = reviewService.createReview(review, userId);
        return ApiResponse.success(reviewId, "리뷰가 등록되었습니다.");
    }
}

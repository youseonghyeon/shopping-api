package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.CreateReviewRequest;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.Review;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.converter.ReviewConverter;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.repository.ReviewRepository;
import com.shop.shoppingapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public List<Review> findReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Long createReview(Review review) {
        return null;
    }

    @Transactional
    public Long createReview(CreateReviewRequest createReviewRequest, Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product findProduct = productRepository.findById(createReviewRequest.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        // TODO 해당 user가 상품을 구매했는지 검증 로직 추가

        Review review = ReviewConverter.toEntity(createReviewRequest, findUser, findProduct);
        return reviewRepository.save(review).getId();
    }
}

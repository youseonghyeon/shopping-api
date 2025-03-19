package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.CreateReviewRequest;
import com.shop.shoppingapi.entity.*;
import com.shop.shoppingapi.exception.BusinessValidationException;
import com.shop.shoppingapi.repository.OrderItemRepository;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.repository.ReviewRepository;
import com.shop.shoppingapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final OrderValidationService orderValidationService;

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public List<Review> findReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional
    public Long createReview(CreateReviewRequest createReviewRequest, Long userId) {
        Long productId = createReviewRequest.getProductId();
        Long orderItemId = createReviewRequest.getOrderItemId();
        User findUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found. userId: " + userId));
        Product findProduct = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found. productId: " + productId));
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new EntityNotFoundException("OrderItem not found. orderItemId: " + orderItemId));
        // validate user has purchased product
        orderValidationService.validateUserHasOrder(orderItemId, userId);
        if (orderItem.getReview() != null) {
            throw new BusinessValidationException("이미 리뷰를 작성한 상품입니다.");
        }
        // create review
        Review review = ReviewConverter.toEntity(createReviewRequest, findUser, findProduct, orderItem);
        Review savedReview = reviewRepository.save(review);
        return savedReview.getId();
    }
}

package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Review;
import com.shop.shoppingapi.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> findReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}

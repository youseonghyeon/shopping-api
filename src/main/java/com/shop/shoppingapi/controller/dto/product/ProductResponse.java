package com.shop.shoppingapi.controller.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shop.shoppingapi.controller.dto.ReviewResponse;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {

    @JsonProperty("id")
    private Long productId; // 제품 ID
    @JsonProperty("name")
    private String productName; // 제품 이름
    private String titleImage; // 타이틀 이미지 URL
    private String title; // 제품 타이틀 또는 간단 설명
    private BigDecimal price; // 제품 가격
    private Double discountRate;
    private BigDecimal discountedPrice;

    @Builder.Default
    private List<ReviewResponse> reviews = new ArrayList<>();


    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .titleImage(product.getTitleImage())
                .title(product.getTitle())
                .price(product.getPrice())
                .discountRate(product.getDiscountRate())
                .discountedPrice(product.getDiscountedPrice()).build();
    }

    public static ProductResponse from(Product product, boolean includeReview) {
        ProductResponseBuilder productResponseBuilder = ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .titleImage(product.getTitleImage())
                .title(product.getTitle())
                .price(product.getPrice())
                .discountRate(product.getDiscountRate())
                .discountedPrice(product.getDiscountedPrice());
        if (includeReview) {
            List<Review> reviews = product.getReviews();
            List<ReviewResponse> reviewResponses = reviews.stream().map(ReviewResponse::from).toList();
            productResponseBuilder.reviews(reviewResponses);
        }
        return productResponseBuilder.build();
    }


}

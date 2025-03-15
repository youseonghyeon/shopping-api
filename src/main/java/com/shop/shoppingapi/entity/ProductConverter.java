package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.product.CreateProductRequest;
import com.shop.shoppingapi.redis.dto.SimpleProduct;

import java.math.BigDecimal;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class ProductConverter {

    public static Product toEntity(CreateProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .titleImage(request.getTitleImage())
                .title(request.getTitle())
                .price(request.getPrice())
                .description(request.getDescription())
                .category(request.getCategory())
                .stock(request.getStock())
                .build();
    }

    public static Product toEntity(String name, String titleImage, String title, BigDecimal price, String description, String category, Integer stock, Double discountRate) {
        return Product.builder()
                .name(name)
                .titleImage(titleImage)
                .title(title)
                .price(price)
                .description(description)
                .category(category)
                .stock(stock)
                .discountRate(discountRate)
                .build();
    }

    public static SimpleProduct toSimpleProductFromEntity(Product product) {
        BigDecimal price = product.getPrice();
        Double discountRate = product.getDiscountRate();
        if (price == null || discountRate == null) {
            throw new IllegalArgumentException("price or discountRate is null");
        }
        BigDecimal discountedPrice = price.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(product.getDiscountRate())));
        return SimpleProduct.builder()
                .productId(product.getId())
                .originalPrice(price)
                .discountRate(discountRate)
                .discountedPrice(discountedPrice)
                .productName(product.getName())
                .productTitleImage(product.getTitleImage())
                .build();
    }
}

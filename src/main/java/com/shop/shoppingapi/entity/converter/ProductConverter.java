package com.shop.shoppingapi.entity.converter;

import com.shop.shoppingapi.controller.dto.product.CreateProductRequest;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.redis.dto.SimpleProduct;

import java.math.BigDecimal;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 */
public class ProductConverter {

    public static Product toEntity(CreateProductRequest request) {
        return new Product(
                request.getName(),
                request.getTitleImage(),
                request.getTitle(),
                request.getPrice(),
                request.getDescription(),
                request.getCategory(),
                request.getStock()
        );
    }

    public static Product toEntity(String name, String titleImage, String title, BigDecimal price, String description, String category, Integer stock, Double discountRate) {
        return new Product(name, titleImage, title, price, description, category, stock, discountRate);
    }

    public static SimpleProduct toSimpleProductFromEntity(Product product) {
        BigDecimal price = product.getPrice();
        Double discountRate = product.getDiscountRate();
        if (price == null || discountRate == null) {
            throw new IllegalArgumentException("price or discountRate is null");
        }
        BigDecimal discountedPrice = price.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(product.getDiscountRate())));
        return new SimpleProduct(product.getId(), price, discountRate, discountedPrice, product.getName(), product.getTitleImage());
    }
}

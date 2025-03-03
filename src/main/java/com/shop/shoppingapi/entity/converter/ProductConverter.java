package com.shop.shoppingapi.entity.converter;

import com.shop.shoppingapi.controller.dto.CreateProductRequest;
import com.shop.shoppingapi.entity.Product;

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

    public static Product toEntity(String name, String titleImage, String title, BigDecimal price, String description, String category, Integer stock) {
        return new Product(name, titleImage, title, price, description, category, stock);
    }
}

package com.shop.shoppingapi.entity;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;

public class ProductConverterForTest {

    public static Product toEntity(@Nullable Long id, String name, String titleImage, String title, BigDecimal price, String description, String category, Integer stock) {
        return new Product(id, name, titleImage, title, price, description, category, stock);
    }

    public static Product toEmptyEntity() {
        return new Product(null, null, null, null, null, null, null, null);
    }
}

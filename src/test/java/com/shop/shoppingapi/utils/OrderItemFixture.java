package com.shop.shoppingapi.utils;

import com.shop.shoppingapi.entity.OrderItem;
import com.shop.shoppingapi.entity.OrderItemConverter;
import com.shop.shoppingapi.entity.Product;

public class OrderItemFixture {

    private OrderItemFixture() {}

    public static OrderItem toOrderItem(Product product) {
        return OrderItemConverter.toEntity(product, 1, product.getPrice(), product.getDiscountRate(), product.getDiscountedPrice(), product.getDiscountedPrice());
    }
}

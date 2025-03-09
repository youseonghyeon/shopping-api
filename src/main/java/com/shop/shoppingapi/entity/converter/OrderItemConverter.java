package com.shop.shoppingapi.entity.converter;

import com.shop.shoppingapi.controller.dto.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.OrderItem;
import com.shop.shoppingapi.entity.Product;

import java.math.BigDecimal;

public class OrderItemConverter {

    public static OrderItem toEntity(Order baseOrder, SubmitOrderRequest.OrderItemRequest orderItemRequest, Product product) {
        BigDecimal totalPrice = product.getDiscountedPrice().multiply(new BigDecimal(orderItemRequest.getQuantity()));
        return OrderItem.builder()
                .order(baseOrder)
                .productId(orderItemRequest.getProductId())
                .quantity(orderItemRequest.getQuantity())
                .unitPrice(product.getPrice())
                .discountRate(product.getDiscountRate())
                .discountedPrice(product.getDiscountedPrice())
                .totalPrice(totalPrice)
                .build();


    }
}

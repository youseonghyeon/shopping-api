package com.shop.shoppingapi.controller.dto;

import com.shop.shoppingapi.entity.Order;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OrderResponse {

    public static OrderResponse of(Order order) {
//        return new OrderResponse(order.getId(), order.getProductName(), order.getPrice(), order.getQuantity(), order.getCreatedAt());
        // TODO Not implemented yet
        throw new UnsupportedOperationException("Not implemented yet");
    }

}

package com.shop.shoppingapi.controller.dto.cart;

import lombok.Getter;

@Getter
public class UpdateCartRequest {
    private Long productId;
    private Integer quantity;
}

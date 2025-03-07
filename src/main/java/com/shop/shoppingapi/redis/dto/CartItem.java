package com.shop.shoppingapi.redis.dto;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {
    private Long productId;
    private int quantity;
}

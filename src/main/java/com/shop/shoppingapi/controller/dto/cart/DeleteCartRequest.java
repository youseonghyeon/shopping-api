package com.shop.shoppingapi.controller.dto.cart;

import lombok.Getter;

import java.util.List;

@Getter
public class DeleteCartRequest {
    private List<Long> productIds;
}

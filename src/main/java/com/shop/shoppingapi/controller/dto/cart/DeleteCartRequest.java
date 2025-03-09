package com.shop.shoppingapi.controller.dto.cart;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeleteCartRequest {
    private List<Long> productIds = new ArrayList<>();
}

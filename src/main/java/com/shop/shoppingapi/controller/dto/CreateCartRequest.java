package com.shop.shoppingapi.controller.dto;

import lombok.Data;

@Data
public class CreateCartRequest {

    private Long productId;
    private Integer quantity = 1;

}

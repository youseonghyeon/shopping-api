package com.shop.shoppingapi.controller.dto;

import lombok.Data;

@Data
public class CreateReviewRequest {

    private Long productId;
    private String content;
    private Integer rating;

}

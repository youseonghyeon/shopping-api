package com.shop.shoppingapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    private Long orderItemId;
    private Long productId;
    private String content;
    private Integer rating;

}

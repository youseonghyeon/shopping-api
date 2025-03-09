package com.shop.shoppingapi.controller.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shop.shoppingapi.entity.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {

    @JsonProperty("id")
    private Long productId; // 제품 ID
    @JsonProperty("name")
    private String productName; // 제품 이름
    private String titleImage; // 타이틀 이미지 URL
    private String title; // 제품 타이틀 또는 간단 설명
    private BigDecimal price; // 제품 가격


    public static ProductResponse of(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getTitleImage(),
                product.getTitle(),
                product.getPrice()
        );
    }


}

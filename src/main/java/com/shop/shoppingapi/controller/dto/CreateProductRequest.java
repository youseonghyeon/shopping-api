package com.shop.shoppingapi.controller.dto;

import com.shop.shoppingapi.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    private String name; // 제품 이름
    private String titleImage; // 타이틀 이미지 URL
    private String title; // 제품 타이틀 또는 간단 설명
    private BigDecimal price; // 제품 가격
    private String description; // 상세 설명
    private String category; // 제품 카테고리
    private Integer stock; // 재고 수량

}

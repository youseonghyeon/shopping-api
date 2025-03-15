package com.shop.shoppingapi.controller.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotEmpty(message = "제품 이름은 필수입니다.")
    private String name; // 제품 이름

    @NotEmpty(message = "타이틀 이미지 URL은 필수입니다.")
    private String titleImage; // 타이틀 이미지 URL

    @NotEmpty(message = "제품 타이틀은 필수입니다.")
    private String title; // 제품 타이틀 또는 간단 설명

    @NotNull(message = "제품 가격은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "제품 가격은 0 이상이어야 합니다.")
    private BigDecimal price; // 제품 가격

    private String description; // 상세 설명
    private String category; // 제품 카테고리
    private Integer stock; // 재고 수량

}

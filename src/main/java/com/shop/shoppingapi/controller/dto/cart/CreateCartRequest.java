package com.shop.shoppingapi.controller.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    @Min(value = 1, message = "유효한 상품 ID를 입력해 주세요.")
    private Long productId;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "유효한 수량을 입력해 주세요.")
    private Integer quantity;

}

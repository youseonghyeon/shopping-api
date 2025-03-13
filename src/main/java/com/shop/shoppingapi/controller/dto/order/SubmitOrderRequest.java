package com.shop.shoppingapi.controller.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SubmitOrderRequest {

    @Valid
    @NotNull(message = "배송 정보는 필수입니다.")
    private ShippingInfo shippingInfo;

    @NotBlank(message = "결제 수단을 선택해 주세요.")
    private String paymentMethod; // 예: "bank", "card", "mobile"

    @Min(value = 0, message = "사용 포인트는 0 이상이어야 합니다.")
    private Integer usedPoints; // 사용한 포인트

    @NotEmpty(message = "주문 상품 정보가 없습니다.")
    @Valid
    private List<OrderItemRequest> items;

    @NotNull(message = "총 배송비는 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "총 배송비는 0 이상이어야 합니다.")
    private BigDecimal shippingFee; // 배송비

    @NotNull(message = "총 상품가격은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "총 상품가격은 0 이상이어야 합니다.")
    private BigDecimal totalProductPrice; // 할인 적용 후 상품 총합

    @NotNull(message = "총 할인 금액은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "총 할인 금액은 0 이상이어야 합니다.")
    private BigDecimal discountSum; // 총 할인 금액

    @NotNull(message = "총 결제 금액은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "총 결제 금액은 0보다 커야 합니다.")
    private BigDecimal totalPayment; // (totalProductPrice - usedPoints + shippingFee)

    // 쿠폰 코드는 선택사항이므로 별도의 검증 어노테이션은 붙이지 않음.
    private String couponCode;

    public List<Long> getProductIds() {
        return items.stream()
                .map(OrderItemRequest::getProductId)
                .toList();
    }

    @Data
    public static class ShippingInfo {
        @NotBlank(message = "수령인 이름은 필수입니다.")
        private String recipientName;

        @NotBlank(message = "주소는 필수입니다.")
        private String address;

        @NotBlank(message = "연락처는 필수입니다.")
        private String phone;

        // 배송 요청사항은 선택 사항
        private String deliveryRequest;
    }

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "상품 ID는 필수입니다.")
        @Min(value = 1, message = "유효한 상품 ID를 입력해 주세요.")
        private Long productId;

        @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다.")
        private int quantity;

        @NotNull(message = "상품 가격은 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "상품 가격은 0보다 커야 합니다.")
        private BigDecimal price; // 할인 적용 후 가격
    }
}

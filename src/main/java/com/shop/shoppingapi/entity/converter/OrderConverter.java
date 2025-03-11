package com.shop.shoppingapi.entity.converter;

import com.shop.shoppingapi.controller.dto.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.User;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderConverter {

    public static Order toEntity(@NonNull SubmitOrderRequest request, @NonNull String orderNumber, @NonNull User user) {

        // 1. Null 검증 (필수 필드)
        validateRequest(request);
        validateField("orderNumber", orderNumber);
        validateField("userId", user.getId());

        // 2. 주문 헤더(Order) 엔티티 생성
        return Order.builder()
                .orderNumber(orderNumber)
                .buyer(user)
                .recipientName(request.getShippingInfo().getRecipientName())
                .address(request.getShippingInfo().getAddress())
                .phone(request.getShippingInfo().getPhone())
                .deliveryRequest(request.getShippingInfo().getDeliveryRequest())
                .paymentMethod(request.getPaymentMethod())
                .usedPoints(request.getUsedPoints())
                .totalProductPrice(request.getTotalProductPrice())
                .shippingFee(request.getShippingFee())
                .discountSum(request.getDiscountSum())
                .finalPayment(request.getTotalPayment())
                .orderStatus("pending")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static void validateRequest(SubmitOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("SubmitOrderRequest 객체는 null일 수 없습니다.");
        }
        if (request.getShippingInfo() == null) {
            throw new IllegalArgumentException("ShippingInfo 객체는 null일 수 없습니다.");
        }
        validateField("recipientName", request.getShippingInfo().getRecipientName());
        validateField("address", request.getShippingInfo().getAddress());
        validateField("phone", request.getShippingInfo().getPhone());
        validateField("paymentMethod", request.getPaymentMethod());
        validateField("usedPoints", request.getUsedPoints());
        validateField("totalProductPrice", request.getTotalProductPrice());
        validateField("shippingFee", request.getShippingFee());
        validateField("discountSum", request.getDiscountSum());
        validateField("totalPayment", request.getTotalPayment());
    }

    private static void validateField(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(fieldName + " 값이 null일 수 없습니다.");
        }
        if (value instanceof String && !StringUtils.hasText((String) value)) {
            throw new IllegalArgumentException(fieldName + " 값이 비어 있을 수 없습니다.");
        }
        if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " 값은 0 이상이어야 합니다.");
        }
    }
}

package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class OrderItemConverter {

    public static OrderItem toEntity(@NonNull Order baseOrder, @NonNull SubmitOrderRequest.OrderItemRequest orderItemRequest, @NonNull Product product) {

        // 1. Null 및 필수 필드 검증
        validateRequest(orderItemRequest);
        validateProduct(product);
        validateField("baseOrder", baseOrder);

        // 2. 총 가격 계산
        BigDecimal totalPrice = product.getDiscountedPrice().multiply(new BigDecimal(orderItemRequest.getQuantity()));
        validateField("totalPrice", totalPrice);

        // 3. OrderItem 엔티티 생성
        return OrderItem.builder()
                .order(baseOrder)
                .product(product)
                .quantity(orderItemRequest.getQuantity())
                .unitPrice(product.getPrice())
                .discountRate(product.getDiscountRate())
                .discountedPrice(product.getDiscountedPrice())
                .totalPrice(totalPrice)
                .build();
    }

    private static void validateRequest(SubmitOrderRequest.OrderItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("OrderItemRequest 객체는 null일 수 없습니다.");
        }
        validateField("productId", request.getProductId());
        validateField("quantity", request.getQuantity());
        validateField("price", request.getPrice());

        if (request.getQuantity() < 1) {
            throw new IllegalArgumentException("상품 수량은 최소 1개 이상이어야 합니다.");
        }
    }

    private static void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product 객체는 null일 수 없습니다.");
        }
        validateField("price", product.getPrice());
        validateField("discountRate", product.getDiscountRate());
        validateField("discountedPrice", product.getDiscountedPrice());
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
        if (value instanceof Integer && ((Integer) value) < 0) {
            throw new IllegalArgumentException(fieldName + " 값은 0 이상이어야 합니다.");
        }
    }
}

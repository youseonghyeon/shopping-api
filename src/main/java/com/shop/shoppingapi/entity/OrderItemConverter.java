package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

        // 2. 총 가격 계산
        BigDecimal totalPrice = product.getDiscountedPrice().multiply(new BigDecimal(orderItemRequest.getQuantity()));

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

    public static OrderItem toEntity(Product product, int quantity, BigDecimal unitPrice, Double discountRate, BigDecimal discountedPrice, BigDecimal totalPrice) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .discountRate(discountRate)
                .discountedPrice(discountedPrice)
                .totalPrice(totalPrice)
                .build();
    }

}

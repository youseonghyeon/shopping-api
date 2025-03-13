package com.shop.shoppingapi.controller.dto.order;

import com.shop.shoppingapi.controller.dto.product.ProductResponse;
import com.shop.shoppingapi.entity.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {

    private Long id;
    private ProductResponse productResponse;
    private int quantity;
    private BigDecimal unitPrice;
    private Double discountRate;
    private BigDecimal discountedPrice;
    private BigDecimal totalPrice;

    public static OrderItemResponse from(OrderItem orderItem, boolean includeProduct) {
        OrderItemResponseBuilder builder = OrderItemResponse.builder()
                .id(orderItem.getId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .discountRate(orderItem.getDiscountRate())
                .discountedPrice(orderItem.getDiscountedPrice())
                .totalPrice(orderItem.getTotalPrice());
        if (includeProduct) {
            builder.productResponse(ProductResponse.from(orderItem.getProduct()));
        }
        return builder.build();
    }
}

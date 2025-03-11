package com.shop.shoppingapi.controller.dto.order;

import com.shop.shoppingapi.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Builder
public class OrderResponse {

    private String orderNumber; // 고유 주문 번호
    private String recipientName; // 수령인 이름
    private String address; // 배송 주소
    private String phone; // 연락처
    private String deliveryRequest; // 배송 요청사항
    private String paymentMethod; // 결제 수단 (bank, card, mobile)
    private Integer usedPoints; // 사용한 포인트
    private BigDecimal totalProductPrice; // 할인 적용 후 상품 총액
    private BigDecimal shippingFee; // 배송비
    private BigDecimal discountSum; // 총 할인 금액
    private BigDecimal finalPayment; // 최종 결제 금액 (totalProductPrice - usedPoints + shippingFee)
    private String orderStatus; // 주문 상태 (예: pending, completed, cancelled)
    private LocalDateTime createdAt; // 주문 생성 일시
    private LocalDateTime updatedAt; // 마지막 수정 일시
    private List<OrderItemResponse> orderItems; // 주문 아이템 리스트

    public static OrderResponse from(Order order, boolean includeOrderItems, boolean includeProduct) {
        OrderResponseBuilder orderResponseBuilder = OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .recipientName(order.getRecipientName())
                .address(order.getAddress())
                .phone(order.getPhone())
                .deliveryRequest(order.getDeliveryRequest())
                .paymentMethod(order.getPaymentMethod())
                .usedPoints(order.getUsedPoints())
                .totalProductPrice(order.getTotalProductPrice())
                .shippingFee(order.getShippingFee())
                .discountSum(order.getDiscountSum())
                .finalPayment(order.getFinalPayment())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        if (includeOrderItems) {
            orderResponseBuilder.orderItems(
                    Optional.ofNullable(order.getOrderItems())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(orderItem -> OrderItemResponse.from(orderItem, includeProduct))
                            .toList()
            );
        }

        return orderResponseBuilder.build();
    }

}

package com.shop.shoppingapi.producer.dto;

import com.shop.shoppingapi.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryMessage {

    private String orderNumber;
    private Long buyerId;
    private String recipientName;
    private String address;
    private String phone;
    private String deliveryRequest; // 배송 요청사항
    private String orderStatus; // 주문 상태 (예: pending, completed, cancelled)
    private List<OrderItemInfo> orderItemInfos; // 주문 아이템 리스트

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemInfo {
        private ProductInfo productInfo;
        private int quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfo {
        private Long productId;
        private String productName;
    }

    public static DeliveryMessage toMessage(Order order) {

        return DeliveryMessage.builder()
                .orderNumber(order.getOrderNumber())
                .buyerId(order.getBuyer().getId())
                .recipientName(order.getRecipientName())
                .address(order.getAddress())
                .phone(order.getPhone())
                .deliveryRequest(order.getDeliveryRequest())
                .orderStatus(order.getOrderStatus())
                .orderItemInfos(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemInfo.builder()
                                .productInfo(ProductInfo.builder()
                                        .productId(orderItem.getProduct().getId())
                                        .productName(orderItem.getProduct().getName())
                                        .build())
                                .quantity(orderItem.getQuantity())
                                .build())
                        .toList())
                .build();
    }
}

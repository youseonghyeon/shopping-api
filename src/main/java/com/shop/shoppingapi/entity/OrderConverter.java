package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class OrderConverter {

    public static Order toEntity(@NonNull SubmitOrderRequest request, @NonNull String orderNumber, @NonNull User user) {
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
                .build();
    }

    public static Order toEntity(String orderNumber, User buyer, String recipientName, String address, String phone, String deliveryRequest, String paymentMethod, Integer usedPoints, BigDecimal totalProductPrice, BigDecimal shippingFee, BigDecimal discountSum, BigDecimal finalPayment, String orderStatus, List<OrderItem> orderItems) {
        return Order.builder()
                .orderNumber(orderNumber)
                .buyer(buyer)
                .recipientName(recipientName)
                .address(address)
                .phone(phone)
                .deliveryRequest(deliveryRequest)
                .paymentMethod(paymentMethod)
                .usedPoints(usedPoints)
                .totalProductPrice(totalProductPrice)
                .shippingFee(shippingFee)
                .discountSum(discountSum)
                .finalPayment(finalPayment)
                .orderStatus(orderStatus)
                .orderItems(orderItems)
                .build();
    }
}

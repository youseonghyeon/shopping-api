package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import lombok.NonNull;

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
}

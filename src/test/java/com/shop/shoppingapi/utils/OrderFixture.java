package com.shop.shoppingapi.utils;

import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import com.shop.shoppingapi.entity.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class OrderFixture {

    private static String orderNumberMock = "ORDER123456";
    private static String recipientNameMock = "홍길동";
    private static String addressMock = "서울시 테스트구 테스트로 123";
    private static String phoneMock = "01012345678";
    private static String deliveryRequestMock = "문 앞에 놓아주세요.";
    private static String paymentMethodMock = "CARD";
    private static Integer usedPointsMock = 0;
    private static BigDecimal totalProductPriceMock = BigDecimal.valueOf(30000);
    private static BigDecimal shippingFeeMock = BigDecimal.valueOf(3000);
    private static BigDecimal discountSumMock = BigDecimal.valueOf(1000);
    private static BigDecimal finalPaymentMock = BigDecimal.valueOf(32000);


    public static SubmitOrderRequest toSubmitOrderRequest() {
        return toSubmitOrderRequest(null, null, null, null, null, null, null, null);
    }
    public static SubmitOrderRequest toSubmitOrderRequest(
            @Nullable SubmitOrderRequest.ShippingInfo shippingInfo,
            @Nullable String paymentMethod,
            @Nullable Integer usedPoints,
            @Nullable BigDecimal totalProductPrice,
            @Nullable BigDecimal shippingFee,
            @Nullable BigDecimal discountSum,
            @Nullable BigDecimal finalPayment,
            @NotNull Product product
    ) {
        shippingInfo = Objects.requireNonNullElseGet(shippingInfo, () -> SubmitOrderRequest.ShippingInfo.builder()
                .recipientName(recipientNameMock)
                .address(addressMock)
                .phone(phoneMock)
                .deliveryRequest(deliveryRequestMock)
                .build());

        List<SubmitOrderRequest.OrderItemRequest> items = List.of(
                SubmitOrderRequest.OrderItemRequest.builder()
                        .productId(product.getId())
                        .quantity(1)
                        .build()
        );

        return SubmitOrderRequest.builder()
                .shippingInfo(shippingInfo)
                .items(items)
                .paymentMethod(Objects.isNull(paymentMethod) ? paymentMethodMock : paymentMethod)
                .usedPoints(Objects.isNull(usedPoints) ? usedPointsMock : usedPoints)
                .totalProductPrice(Objects.isNull(totalProductPrice) ? totalProductPriceMock : totalProductPrice)
                .shippingFee(Objects.isNull(shippingFee) ? shippingFeeMock : shippingFee)
                .discountSum(Objects.isNull(discountSum) ? discountSumMock : discountSum)
                .totalPayment(Objects.isNull(finalPayment) ? finalPaymentMock : finalPayment)
                .build();
    }

    public static Order toOrder(
            @Nullable SubmitOrderRequest submitOrderRequest,
            @Nullable String orderNumber,
            @Nullable User user
    ) {
        submitOrderRequest = Objects.requireNonNullElseGet(submitOrderRequest, OrderFixture::toSubmitOrderRequest);
        orderNumber = Objects.isNull(orderNumber) ? orderNumberMock : orderNumber;
        user = Objects.requireNonNullElseGet(user, UserFixture::toUser);

        return OrderConverter.toEntity(submitOrderRequest, orderNumber, user);
    }
}

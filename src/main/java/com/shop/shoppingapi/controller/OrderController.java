package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CustomPagedModelAssembler;
import com.shop.shoppingapi.controller.dto.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.OrderService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final CustomPagedModelAssembler<com.shop.shoppingapi.controller.dto.order.OrderResponse> pagedModelAssembler;

    @GetMapping("/order")
    public ResponseEntity<? extends ApiResponse<?>> getMyOrders(@PageableDefault(size = 10) Pageable pageable, Authentication authentication) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping("/order/submit")
    public ResponseEntity<ApiResponse<Map<String, Long>>> submitOrder(@Validated @RequestBody SubmitOrderRequest submitOrderRequest) {
        Long userId = SecurityUtils.getUserId();
        Long orderId = orderService.submitOrder(userId, submitOrderRequest);
        return ApiResponse.success(Map.of("orderId", orderId), "주문을 성공하였습니다.");
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<? extends ApiResponse<?>> getOrder(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getUserId();
        Order findOrder = orderService.findOrderById(orderId);
        if (!findOrder.getCustomerId().equals(userId)) {
            return ApiResponse.error("주문을 조회할 수 있는 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        return ApiResponse.success(OrderResponse.from(findOrder), "주문 정보를 조회하였습니다.");
    }

    @Data
    @Builder
    public static class OrderResponse {
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
//        private List<OrderItem> orderItems; // 주문 아이템 리스트

        public static OrderResponse from(Order order) {
            return OrderResponse.builder()
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
                    .updatedAt(order.getUpdatedAt())
                    .build();
        }
    }
}

package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CustomPagedModelAssembler;
import com.shop.shoppingapi.controller.dto.order.OrderResponse;
import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final CustomPagedModelAssembler<OrderResponse> pagedModelAssembler;

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<PagedModel<OrderResponse>>> getMyOrders(@PageableDefault(size = 10) Pageable pageable) {
        Long userId = SecurityUtils.getUserId();
        Page<Order> orders = orderService.findOrdersWithOrdersItemsAndProductByUserId(userId, pageable);
        Page<OrderResponse> orderResponses = orders.map(order -> OrderResponse.from(order, true, true));
        return ApiResponse.success(pagedModelAssembler.toModel(orderResponses), "주문 목록을 조회하였습니다.");
    }

    @PostMapping("/order/submit")
    public ResponseEntity<ApiResponse<Map<String, Long>>> submitOrder(@Validated @RequestBody SubmitOrderRequest submitOrderRequest) {
        Long userId = SecurityUtils.getUserId();
        Long orderId = orderService.submitOrder(userId, submitOrderRequest);
        return ApiResponse.success(Map.of("orderId", orderId), "주문을 성공하였습니다.");
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<? extends ApiResponse<?>> getOrder(@PathVariable Long orderId) {
        Long currentUserId = SecurityUtils.getUserId();
        Order findOrder = orderService.findOrderById(orderId);
        if (!currentUserId.equals(findOrder.getBuyer().getId())) {
            return ApiResponse.error("주문을 조회할 수 있는 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        return ApiResponse.success(OrderResponse.from(findOrder, false, false), "주문 정보를 조회하였습니다.");
    }

}

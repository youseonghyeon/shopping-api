package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CustomPagedModelAssembler;
import com.shop.shoppingapi.controller.dto.SubmitOrderRequest;
import com.shop.shoppingapi.controller.dto.order.OrderResponse;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final CustomPagedModelAssembler<OrderResponse> pagedModelAssembler;

    @GetMapping("/order")
    public ResponseEntity<? extends ApiResponse<?>> getMyOrders(@PageableDefault(size = 10) Pageable pageable, Authentication authentication) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping("/order/submit")
    public ResponseEntity<? extends ApiResponse<?>> submitOrder(@Validated @RequestBody SubmitOrderRequest submitOrderRequest) {
        Long userId = SecurityUtils.getUserId();
        Long orderId = orderService.submitOrder(userId, submitOrderRequest);
        log.info("SubmitOrderRequest: {}", submitOrderRequest);
        return ApiResponse.success("Order submitted");
    }
}

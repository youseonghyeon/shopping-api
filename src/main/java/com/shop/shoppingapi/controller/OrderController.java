package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CustomPagedModelAssembler;
import com.shop.shoppingapi.controller.dto.OrderResponse;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CustomPagedModelAssembler<OrderResponse> pagedModelAssembler;


    @GetMapping("/my-orders")
    public ResponseEntity<? extends ApiResponse<?>> getMyOrders(@PageableDefault(size = 10) Pageable pageable, Authentication authentication) {
        if (authentication == null) {
            return ApiResponse.error("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        Page<Order> orders = orderService.findMyOrder(pageable);
        Page<OrderResponse> productsResponse = orders.map(OrderResponse::of);
        return ApiResponse.success(pagedModelAssembler.toModel(productsResponse));
    }
}

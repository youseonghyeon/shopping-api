package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.cart.CartResponse;
import com.shop.shoppingapi.controller.dto.cart.CreateCartRequest;
import com.shop.shoppingapi.controller.dto.cart.DeleteCartRequest;
import com.shop.shoppingapi.controller.dto.cart.UpdateCartRequest;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<CartResponse>>> list() {
        Long userId = SecurityUtils.getUserId();
        List<CartResponse> cartResponses = cartService.findCarts(userId);
        return ApiResponse.success(cartResponses, "장바구니 목록을 조회하였습니다.");
    }

    @GetMapping("/cart/count")
    public ResponseEntity<ApiResponse<Integer>> count() {
        Long userId = SecurityUtils.getUserId();
        int count = cartService.findCartsSize(userId);
        return ApiResponse.success(count, "장바구니 상품 개수를 조회하였습니다.");
    }

    @PostMapping("/cart/create")
    public ResponseEntity<ApiResponse<String>> addCart(@Validated @RequestBody CreateCartRequest createCartRequest) {
        Long userId = SecurityUtils.getUserId();
        log.info("userId: {} CreateCartRequest: {}", userId, createCartRequest);  // 로그 추가

        cartService.addCartItem(userId, createCartRequest.getProductId(), createCartRequest.getQuantity());
        return ApiResponse.success("장바구니에 상품을 추가하였습니다.", "장바구니에 상품을 추가하였습니다.");
    }

    @PostMapping("/cart/delete")
    public ResponseEntity<ApiResponse<String>> deleteCart(@Validated @RequestBody DeleteCartRequest deleteCartRequest) {
        Long userId = SecurityUtils.getUserId();
        cartService.deleteCartItem(userId, deleteCartRequest.getProductIds());
        return ApiResponse.success("장바구니에서 상품을 삭제하였습니다.", "장바구니에서 상품을 삭제하였습니다.");
    }

    @PostMapping("/cart/update")
    public ResponseEntity<ApiResponse<String>> updateCart(@Validated @RequestBody UpdateCartRequest updateCartRequest) {
        Long userId = SecurityUtils.getUserId();
        cartService.updateCartItem(userId, updateCartRequest.getProductId(), updateCartRequest.getQuantity());
        return ApiResponse.success("장바구니에 상품을 수정하였습니다.", "장바구니에 상품을 수정하였습니다.");
    }

}

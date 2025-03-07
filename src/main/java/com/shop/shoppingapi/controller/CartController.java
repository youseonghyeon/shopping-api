package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CreateCartRequest;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.CartService;
import com.shop.shoppingapi.service.ProductService;
import com.shop.shoppingapi.service.dto.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final ProductService productService;
    private final CartService cartService;

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<Cart>>> list() {
        Long userId = SecurityUtils.getUserId();
        List<Cart> carts = cartService.findCarts(userId);
        return ApiResponse.success(carts);
    }

    @PostMapping("/cart/create")
    public ResponseEntity<ApiResponse<String>> create(@RequestBody CreateCartRequest createCartRequest) {
        Long userId = SecurityUtils.getUserId();
        cartService.addCartItem(userId, createCartRequest.getProductId(), createCartRequest.getQuantity());
        return ApiResponse.success("장바구니에 상품을 추가하였습니다.", "장바구니에 상품을 추가하였습니다.");
    }

    @PostMapping("/cart/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestBody Long userId, @RequestBody Long productId) {
        cartService.deleteCartItem(userId, productId);
        return ApiResponse.success("장바구니에서 상품을 삭제하였습니다.", "장바구니에서 상품을 삭제하였습니다.");
    }

    @PostMapping("/cart/update")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody Long userId, @RequestBody Long productId, @RequestBody Integer quantity) {
        cartService.updateCartItem(userId, productId, quantity);
        return ApiResponse.success("장바구니에 상품을 수정하였습니다.", "장바구니에 상품을 수정하였습니다.");
    }

}

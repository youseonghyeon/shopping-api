package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CreateWishlistRequest;
import com.shop.shoppingapi.entity.Wishlist;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import com.shop.shoppingapi.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/wishlist")
    public ResponseEntity<ApiResponse<List<Wishlist>>> getWishlists() {
        Long userId = SecurityUtils.getUserId();
        List<Wishlist> wishlists = wishlistService.findByUserId(userId);
        return ApiResponse.success(wishlists, "위시리스트 목록을 조회하였습니다.");
    }

    @PostMapping("/wishlist/create")
    public ResponseEntity<ApiResponse<Long>> saveWishlist(CreateWishlistRequest createWishlistRequest) {
        Long userId = SecurityUtils.getUserId();
        Long wishlistId = wishlistService.save(userId, createWishlistRequest);
        return ApiResponse.success(wishlistId, "위시리스트에 상품을 추가하였습니다.");
    }

    @GetMapping("/wishlist/exist")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> existWishlist(@RequestParam("productId") Long productId) {
        Long userId = SecurityUtils.getUserId();
        boolean existsProductInWishlist = wishlistService.existsByUserIdAndProductId(userId, productId);
        return ApiResponse.success(Map.of("exist", existsProductInWishlist), "위시리스트에 상품이 존재합니다.");
    }


}

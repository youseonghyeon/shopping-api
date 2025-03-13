package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.wishlist.CreateWishlistRequest;
import com.shop.shoppingapi.controller.dto.wishlist.DeleteWishlistRequest;
import com.shop.shoppingapi.controller.dto.wishlist.WishlistResponse;
import com.shop.shoppingapi.controller.dto.product.ProductResponse;
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
@RequestMapping("/api")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlists() {
        Long userId = SecurityUtils.getUserId();
        List<Wishlist> wishlists = wishlistService.findWithProductByUserId(userId);
        List<ProductResponse> list = wishlists.stream().map(Wishlist::getProduct).map(ProductResponse::of).toList();
        WishlistResponse wishlistResponse = new WishlistResponse(list);
        return ApiResponse.success(wishlistResponse, "위시리스트 목록을 조회하였습니다.");
    }

    @GetMapping("/wishlist/exist")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> existWishlist(@RequestParam("productId") Long productId) {
        Long userId = SecurityUtils.getUserId();
        boolean existsProductInWishlist = wishlistService.existsByUserIdAndProductId(userId, productId);
        return ApiResponse.success(Map.of("exist", existsProductInWishlist), "위시리스트에 상품이 존재합니다.");
    }

    @PostMapping("/wishlist/create")
    public ResponseEntity<ApiResponse<String>> saveWishlist(@RequestBody CreateWishlistRequest createWishlistRequest) {
        Long userId = SecurityUtils.getUserId();
        wishlistService.save(userId, createWishlistRequest.getProductId());
        return ApiResponse.success("위시리스트에 상품을 추가하였습니다.");
    }

    @PostMapping("/wishlist/delete")
    public ResponseEntity<ApiResponse<String>> deleteWishlist(@RequestBody DeleteWishlistRequest deleteWishlistRequest) {
        Long userId = SecurityUtils.getUserId();
        wishlistService.deleteByUserIdAndProductId(userId, deleteWishlistRequest.getProductId());
        return ApiResponse.success("위시리스트에서 상품을 삭제하였습니다.");
    }


}

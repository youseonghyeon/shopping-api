package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.CreateWishlistRequest;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.Wishlist;
import com.shop.shoppingapi.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional
    public Long save(Long userId, CreateWishlistRequest createWishlistRequest) {
        validateAlreadySaved(createWishlistRequest, userId);
        User findUser = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        Product findProduct = productService.findProductById(createWishlistRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Wishlist wishlist = Wishlist.create(findUser, findProduct);
        return wishlistRepository.save(wishlist).getId();
    }

    @Transactional(readOnly = true)
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId);
    }

    private void validateAlreadySaved(CreateWishlistRequest createWishlistRequest, Long userId) {
        Long productId = createWishlistRequest.getProductId();
        if (wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }
    }

    public List<Wishlist> findByUserId(Long userId) {
        return wishlistRepository.findByUser_Id(userId);
    }
}

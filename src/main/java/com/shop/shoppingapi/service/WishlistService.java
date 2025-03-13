package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.Wishlist;
import com.shop.shoppingapi.repository.WishlistRepository;
import com.shop.shoppingapi.security.utils.LazyLoadingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    final int MAX_WISHLIST_SIZE = 20;
    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Wishlist> findByUserId(Long userId) {
        return wishlistRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<Wishlist> findWithProductByUserId(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUser_Id(userId);
        LazyLoadingUtils.forceLoad(wishlists, Wishlist::getProduct);
        return wishlists;
    }


    @Transactional(readOnly = true)
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId);
    }

    @Transactional
    public Long save(Long userId, Long productId) {
        User findUser = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        long existCount = wishlistRepository.countWishlistByUser_Id(userId);
        if (existCount >= MAX_WISHLIST_SIZE) {
            throw new IllegalArgumentException("위시리스트에 추가할 수 있는 상품의 개수는 최대 " + MAX_WISHLIST_SIZE + "개입니다.");
        }
        if (wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }
        Product findProduct = productService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Wishlist wishlist = Wishlist.create(findUser, findProduct);
        return wishlistRepository.save(wishlist).getId();
    }

    @Transactional
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        if (!wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new IllegalArgumentException("찜한 상품이 존재하지 않습니다.");
        }
        wishlistRepository.deleteByUser_IdAndProduct_Id(userId, productId);
    }
}

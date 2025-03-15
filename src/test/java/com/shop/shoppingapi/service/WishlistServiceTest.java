package com.shop.shoppingapi.service;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.Wishlist;
import com.shop.shoppingapi.repository.WishlistRepository;
import com.shop.shoppingapi.utils.ProductFixture;
import com.shop.shoppingapi.utils.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WishlistServiceTest extends IntegrationTestSupport {

    @Autowired
    WishlistService wishlistService;

    @Autowired
    WishlistRepository wishlistRepository;

    @Test
    @DisplayName("위시리스트 추가 성공")
    void saveWishlist_Success() {
        // given
        User user = super.save(UserFixture.toUser());
        Product product = super.save(ProductFixture.toProduct());
        // when
        Long wishlistId = wishlistService.save(user.getId(), product.getId());
        // then
        Long id = wishlistRepository.findAll().get(0).getId();
        assertEquals(wishlistId, id);
        Wishlist wishlist = wishlistRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 위시리스트가 존재하지 않습니다."));
        assertNotNull(wishlist);
        assertEquals(user.getId(), wishlist.getUser().getId());
        assertEquals(product.getId(), wishlist.getProduct().getId());
    }

    @Test
    @DisplayName("위시리스트에 상품이 존재하는지 조회 성공")
    void existsByUserIdAndProductId() {
        // given
        User user = super.save(UserFixture.toUser());
        Product product = super.save(ProductFixture.toProduct());
        wishlistService.save(user.getId(), product.getId());
        // when
        boolean result = wishlistService.existsByUserIdAndProductId(user.getId(), product.getId());
        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("위시리스트에 상품이 존재하지 않는 상품 조회 성공")
    void notExistsByUserIdAndProductId() {
        // given
        User user = super.save(UserFixture.toUser());
        Product product1 = super.save(ProductFixture.toProduct());
        Product product2 = super.save(ProductFixture.toProduct()); // 다른 상품
        wishlistService.save(user.getId(), product1.getId());
        // when : product2는 위시리스트에 존재하지 않음
        boolean result = wishlistService.existsByUserIdAndProductId(user.getId(), product2.getId());
        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("위시리스트에서 상품 삭제 성공")
    void deleteWishlist_Success() {
        // given
        User user = super.save(UserFixture.toUser());
        Product product = super.save(ProductFixture.toProduct());
        wishlistService.save(user.getId(), product.getId());
        // when
        wishlistService.deleteByUserIdAndProductId(user.getId(), product.getId());
        // then
        List<Wishlist> byUserId = wishlistService.findByUserId(user.getId());
        assertEquals(0, byUserId.size());
    }

}

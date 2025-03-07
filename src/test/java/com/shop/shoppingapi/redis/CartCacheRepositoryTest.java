package com.shop.shoppingapi.redis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CartCacheRepositoryTest {

    @Autowired
    private CartCacheRepository cartCacheRepository;

    private final Long userId = 1L;
    private final Long productId1 = 101L;
    private final Long productId2 = 202L;

    @BeforeEach
    public void setup() {
        // 각 테스트 시작 전에 해당 사용자의 장바구니를 초기화합니다.
        cartCacheRepository.clearCart(userId);
    }

    @Test
    public void testAddItemToCartAndGetCart() {
        // 장바구니에 아이템 추가
        cartCacheRepository.addItemToCart(userId, productId1, 3);
        cartCacheRepository.addItemToCart(userId, productId2, 5);

        // 장바구니 조회
        Map<Object, Object> cart = cartCacheRepository.getCart(userId);
        assertNotNull(cart);
        // 두 상품이 추가되어야 하므로 size가 2여야 함
        assertEquals(2, cart.size());
        // 각 상품의 수량 확인
        assertEquals(3, cart.get(productId1));
        assertEquals(5, cart.get(productId2));
    }

    @Test
    public void testRemoveItemFromCart() {
        // 먼저 두 상품 추가
        cartCacheRepository.addItemToCart(userId, productId1, 2);
        cartCacheRepository.addItemToCart(userId, productId2, 4);

        // productId1 상품 삭제
        cartCacheRepository.removeItemFromCart(userId, productId1);
        Map<Object, Object> cart = cartCacheRepository.getCart(userId);
        assertFalse(cart.containsKey(productId1));
        assertTrue(cart.containsKey(productId2));
    }

    @Test
    public void testClearCart() {
        // 상품 추가 후 전체 삭제 테스트
        cartCacheRepository.addItemToCart(userId, productId1, 2);
        cartCacheRepository.addItemToCart(userId, productId2, 4);
        cartCacheRepository.clearCart(userId);
        Map<Object, Object> cart = cartCacheRepository.getCart(userId);
        assertTrue(cart.isEmpty());
    }

    @Test
    public void testGetCartProductIds() {
        // 상품 추가 후, 상품 ID 목록 조회
        cartCacheRepository.addItemToCart(userId, productId1, 1);
        cartCacheRepository.addItemToCart(userId, productId2, 1);
        Set<Object> productIds = cartCacheRepository.getCartProductIds(userId);
        assertEquals(2, productIds.size());
        assertTrue(productIds.contains(productId1));
        assertTrue(productIds.contains(productId2));
    }
}

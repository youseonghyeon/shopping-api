package com.shop.shoppingapi.redis;

import static org.junit.jupiter.api.Assertions.*;

import com.shop.shoppingapi.redis.dto.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CartResponseItemCacheRepositoryTest {

    @Autowired
    private CartCacheRepository cartCacheRepository;

    private final Long userId = 1L;
    private final Long productId1 = 101L;
    private final Long productId2 = 202L;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전, 해당 사용자의 장바구니를 클리어합니다.
        cartCacheRepository.clearCart(userId);
    }

    @Test
    public void testAddOrUpdateCartItem() {
        // 상품 101번을 수량 3으로 추가
        cartCacheRepository.addOrUpdateCartItem(userId, productId1, 3);
        Integer quantity = cartCacheRepository.getCartItemQuantity(userId, productId1);
        assertNotNull(quantity, "상품 101번의 수량은 null이 아니어야 합니다.");
        assertEquals(3, quantity.intValue(), "상품 101번의 수량은 3이어야 합니다.");

        // 상품 101번을 수량 5로 업데이트
        cartCacheRepository.addOrUpdateCartItem(userId, productId1, 5);
        quantity = cartCacheRepository.getCartItemQuantity(userId, productId1);
        assertNotNull(quantity, "업데이트 후 상품 101번의 수량은 null이 아니어야 합니다.");
        assertEquals(5, quantity.intValue(), "상품 101번의 수량은 5로 업데이트되어야 합니다.");
    }

    @Test
    public void testGetCartItems() {
        // 두 개의 상품 추가: 101번 수량 2, 202번 수량 4
        cartCacheRepository.addOrUpdateCartItem(userId, productId1, 2);
        cartCacheRepository.addOrUpdateCartItem(userId, productId2, 4);

        List<CartItem> cartItems = cartCacheRepository.getCartItems(userId);
        assertNotNull(cartItems, "장바구니 맵은 null이 아니어야 합니다.");
        assertEquals(2, cartItems.size(), "장바구니 항목은 2개여야 합니다.");

        // getCartItemQuantity() 메서드를 활용하여 개별 수량 확인
        assertEquals(2, cartCacheRepository.getCartItemQuantity(userId, productId1));
        assertEquals(4, cartCacheRepository.getCartItemQuantity(userId, productId2));
    }

    @Test
    public void testRemoveCartItem() {
        // 두 개의 상품 추가
        cartCacheRepository.addOrUpdateCartItem(userId, productId1, 2);
        cartCacheRepository.addOrUpdateCartItem(userId, productId2, 4);

        // 상품 101번 삭제
        cartCacheRepository.deleteCartItem(userId, productId1);

        // 상품 101번 삭제 후 수량은 null이어야 함
        Integer quantity = cartCacheRepository.getCartItemQuantity(userId, productId1);
        assertNull(quantity, "삭제된 상품 101번의 수량은 null이어야 합니다.");

        // 상품 202번은 그대로 존재해야 함
        assertEquals(4, cartCacheRepository.getCartItemQuantity(userId, productId2));
    }

    @Test
    public void testClearCart() {
        // 두 개의 상품 추가
        cartCacheRepository.addOrUpdateCartItem(userId, productId1, 2);
        cartCacheRepository.addOrUpdateCartItem(userId, productId2, 4);

        // 장바구니 전체 삭제
        cartCacheRepository.clearCart(userId);

        List<CartItem> cartItems = cartCacheRepository.getCartItems(userId);
        // getCartItems()가 null 또는 빈 맵을 반환해야 함
        assertTrue(cartItems == null || cartItems.isEmpty(), "장바구니는 비어 있어야 합니다.");
    }
}

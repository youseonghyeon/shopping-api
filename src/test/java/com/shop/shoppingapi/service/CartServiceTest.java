package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.cart.CartResponse;
import com.shop.shoppingapi.redis.CartCacheRepository;
import com.shop.shoppingapi.redis.dto.CartItem;
import com.shop.shoppingapi.redis.dto.SimpleProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartCacheRepository cartCacheRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("addCartItem - 성공: 존재하는 상품의 장바구니 항목 추가")
    void addCartItem_success() {
        Long userId = 1L;
        Long productId = 100L;
        int quantityToAdd = 3;
        int existingQuantity = 2;

        when(productService.existsProductById(productId)).thenReturn(true);
        when(cartCacheRepository.getCartItemQuantity(userId, productId)).thenReturn(existingQuantity);

        cartService.addCartItem(userId, productId, quantityToAdd);

        verify(cartCacheRepository).addOrUpdateCartItem(userId, productId, existingQuantity + quantityToAdd);
    }

    @Test
    @DisplayName("addCartItem - 실패: 존재하지 않는 상품인 경우 IllegalArgumentException 발생")
    void addCartItem_failure_nonExistingProduct() {
        Long userId = 1L;
        Long productId = 100L;
        int quantityToAdd = 3;

        when(productService.existsProductById(productId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cartService.addCartItem(userId, productId, quantityToAdd)
        );
        assertEquals("존재하지 않는 상품입니다.", exception.getMessage());
        verify(cartCacheRepository, never()).addOrUpdateCartItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("updateCartItem - 성공: 양수 수량인 경우 장바구니 항목 업데이트")
    void updateCartItem_success_update() {
        Long userId = 1L;
        Long productId = 100L;
        Integer newQuantity = 5;

        cartService.updateCartItem(userId, productId, newQuantity);

        verify(cartCacheRepository).addOrUpdateCartItem(userId, productId, newQuantity);
    }

    @Test
    @DisplayName("updateCartItem - 0 이하 수량인 경우 삭제 호출")
    void updateCartItem_zeroQuantity_callsDelete() {
        Long userId = 1L;
        Long productId = 100L;
        Integer newQuantity = 0;

        cartService.updateCartItem(userId, productId, newQuantity);

        verify(cartCacheRepository).deleteCartItem(userId, productId);
    }

    @Test
    @DisplayName("deleteCartItem - 성공: 여러 상품 삭제")
    void deleteCartItem_success() {
        Long userId = 1L;
        List<Long> productIds = Arrays.asList(100L, 101L);

        cartService.deleteCartItem(userId, productIds);

        for (Long productId : productIds) {
            verify(cartCacheRepository).deleteCartItem(userId, productId);
        }
    }

    @Test
    @DisplayName("findCarts - 성공: 카트 항목 조회 및 변환")
    void findCarts_success() {
        Long userId = 1L;
        CartItem cartItem1 = new CartItem(100L, 2); // 가정: CartItem(productId, quantity)
        CartItem cartItem2 = new CartItem(101L, 1);
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);

        when(cartCacheRepository.getCartItems(userId)).thenReturn(cartItems);

        // SimpleProduct 생성자: (Long, BigDecimal, Double, BigDecimal, String, String)
        SimpleProduct simpleProduct1 = new SimpleProduct(
                100L,
                new BigDecimal("1000"),
                0.0,
                new BigDecimal("1000"),
                "Product 100",
                "Description"
        );
        SimpleProduct simpleProduct2 = new SimpleProduct(
                101L,
                new BigDecimal("2000"),
                0.0,
                new BigDecimal("2000"),
                "Product 101",
                "Description"
        );
        Map<Long, SimpleProduct> simpleProductByIds = Map.of(
                100L, simpleProduct1,
                101L, simpleProduct2
        );
        when(productService.findSimpleProductByIds(Set.of(100L, 101L))).thenReturn(simpleProductByIds);

        List<CartResponse> responses = cartService.findCarts(userId);
        assertNotNull(responses);
        assertEquals(2, responses.size());
        // 추가 검증은 CartResponse.join()의 구현에 따라 진행
    }

    @Test
    @DisplayName("findCartsSize - 성공: 장바구니 항목 개수 조회")
    void findCartsSize_success() {
        Long userId = 1L;
        when(cartCacheRepository.getCartSize(userId)).thenReturn(3);

        int size = cartService.findCartsSize(userId);
        assertEquals(3, size);
    }
}

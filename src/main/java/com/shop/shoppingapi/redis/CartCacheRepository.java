package com.shop.shoppingapi.redis;

import com.shop.shoppingapi.redis.dto.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CartCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_HASH_KEY_PREFIX = "CART_LIST:";

    private String getCartKey(Long userId) {
        return CART_HASH_KEY_PREFIX + userId;
    }

    // 상품 추가 또는 수량 업데이트: Hash의 필드로 productId, 값은 quantity
    public void addOrUpdateCartItem(Long userId, Long productId, int quantity) {
        String key = getCartKey(userId);
        redisTemplate.opsForHash().put(key, productId.toString(), quantity);
    }

    // 특정 상품의 수량 조회
    public int getCartItemQuantity(Long userId, Long productId) {
        String key = getCartKey(userId);
        Object value = redisTemplate.opsForHash().get(key, productId.toString());
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    // 전체 장바구니 조회: 키는 productId, 값은 quantity
    public List<CartItem> getCartItems(Long userId) {
        String key = getCartKey(userId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return entries.entrySet().stream()
                .map(entry -> new CartItem(Long.valueOf(entry.getKey().toString()), Integer.parseInt(entry.getValue().toString())))
                .toList();
    }

    public int getCartSize(Long userId) {
        String key = getCartKey(userId);
        return redisTemplate.opsForHash().size(key).intValue();
    }

    // 특정 상품 삭제: productId 필드를 삭제하면 됨
    public void deleteCartItem(Long userId, Long productId) {
        String key = getCartKey(userId);
        redisTemplate.opsForHash().delete(key, productId.toString());
    }

    public void deleteCartItems(Long userId, List<Long> productIds) {
        productIds.forEach(productId -> deleteCartItem(userId, productId));
    }

    // 전체 장바구니 삭제
    public void clearCart(Long userId) {
        String key = getCartKey(userId);
        redisTemplate.delete(key);
    }
}

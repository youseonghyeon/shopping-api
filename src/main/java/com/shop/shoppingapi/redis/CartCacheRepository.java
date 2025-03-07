package com.shop.shoppingapi.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CartCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_LIST_KEY_PREFIX = "CART_LIST:";

    private String getCartKey(Long userId) {
        return CART_LIST_KEY_PREFIX + userId;
    }

    // 추가
    public void addItemToCart(Long userId, Long productId, int quantity) {
        String cartKey = getCartKey(userId);
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        hashOps.put(cartKey, productId, quantity);
    }

    // 조회
    public Map<Object, Object> getCart(Long userId) {
        String cartKey = getCartKey(userId);
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        return hashOps.entries(cartKey);
    }

    // 삭제
    public void removeItemFromCart(Long userId, Long productId) {
        String cartKey = getCartKey(userId);
        redisTemplate.opsForHash().delete(cartKey, productId);
    }

    // 전체 삭제
    public void clearCart(Long userId) {
        String cartKey = getCartKey(userId);
        redisTemplate.delete(cartKey);
    }

    // 상품 ID 전체 조회
    public Set<Object> getCartProductIds(Long userId) {
        String cartKey = getCartKey(userId);
        return redisTemplate.opsForHash().keys(cartKey);
    }


}

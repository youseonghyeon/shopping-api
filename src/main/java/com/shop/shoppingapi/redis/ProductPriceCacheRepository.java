package com.shop.shoppingapi.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductPriceCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PRODUCT_PRICE_PREFIX = "PRODUCT_PRICE:";

    private HashOperations<String, Object, Object> getHashOps() {
        return redisTemplate.opsForHash();
    }

    // 상품 가격 저장
    public void saveProductPrice(Long productId, BigDecimal originalPrice, BigDecimal discountRate) {
        Map<String, String> priceData = new HashMap<>();
        priceData.put("originalPrice", originalPrice.toString());
        priceData.put("discountRate", discountRate.toString());
        BigDecimal discountedPrice = originalPrice.multiply(BigDecimal.ONE.subtract(discountRate));
        priceData.put("discountedPrice", discountedPrice.toString());
        priceData.put("lastUpdated", Instant.now().toString());

        getHashOps().putAll(PRODUCT_PRICE_PREFIX + ":" + productId, priceData);
    }

    // 단일 상품 가격 조회
    public Map<Object, Object> getProductPrice(Long productId) {
        return getHashOps().entries(PRODUCT_PRICE_PREFIX + ":" + productId);
    }

    // 여러 상품 가격 조회
    public Map<Long, Map<Object, Object>> getMultipleProductPrices(Iterable<Long> productIds) {
        Map<Long, Map<Object, Object>> priceMap = new HashMap<>();
        for (Long productId : productIds) {
            priceMap.put(productId, getProductPrice(productId));
        }
        return priceMap;
    }

    // 삭제
    public void deleteProductPrice(Long productId) {
        redisTemplate.delete(PRODUCT_PRICE_PREFIX + ":" + productId);
    }

}

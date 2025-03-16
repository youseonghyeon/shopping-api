package com.shop.shoppingapi.redis;

import com.shop.shoppingapi.redis.dto.SimpleProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class SimpleProductCacheRepository {

    private static final String PRODUCT_PRICE_PREFIX = "PRODUCT_PRICE:";
    private final RedisTemplate<String, SimpleProduct> redisTemplate;

    private ValueOperations<String, SimpleProduct> getValueOps() {
        return redisTemplate.opsForValue();
    }

    // 상품 가격 저장
    public void save(SimpleProduct simpleProduct) {
        getValueOps().set(PRODUCT_PRICE_PREFIX + ":" + simpleProduct.getProductId(), simpleProduct, 30, TimeUnit.MINUTES);
    }

    // 단일 상품 가격 조회
    public Optional<SimpleProduct> findById(Long productId) {
        return Optional.ofNullable(getValueOps().get(PRODUCT_PRICE_PREFIX + ":" + productId));
    }

    // 여러 상품 가격 조회
    public Map<Long, SimpleProduct> findByIds(Iterable<Long> productIds) {
        Map<Long, SimpleProduct> priceMap = new HashMap<>();
        for (Long productId : productIds) {
            findById(productId)
                    .filter(sp -> !sp.abnormalData())
                    .ifPresent(sp -> priceMap.put(productId, sp));
        }
        return priceMap;
    }

    // 삭제
    public void deleteById(Long productId) {
        redisTemplate.delete(PRODUCT_PRICE_PREFIX + ":" + productId);
    }

}

package com.shop.shoppingapi.redis;

import com.shop.shoppingapi.redis.dto.ProductPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductPriceCacheRepository {

    private final RedisTemplate<String, ProductPrice> redisTemplate;
    private static final String PRODUCT_PRICE_PREFIX = "PRODUCT_PRICE:";

    private ValueOperations<String, ProductPrice> getValueOps() {
        return redisTemplate.opsForValue();
    }

    // 상품 가격 저장
    public void save(ProductPrice productPrice) {
        getValueOps().set(PRODUCT_PRICE_PREFIX + ":" + productPrice.getProductId(), productPrice);
    }

    // 단일 상품 가격 조회
    public Optional<ProductPrice> findById(Long productId) {
        return Optional.ofNullable(getValueOps().get(PRODUCT_PRICE_PREFIX + ":" + productId));
    }

    // 여러 상품 가격 조회
    public Map<Long, ProductPrice> findByIds(Iterable<Long> productIds) {
        Map<Long, ProductPrice> priceMap = new HashMap<>();
        for (Long productId : productIds) {
            findById(productId)
                    .filter(pp -> !pp.abnormalData())
                    .ifPresent(pp -> priceMap.put(productId, pp));
        }
        return priceMap;
    }

    // 삭제
    public void deleteById(Long productId) {
        redisTemplate.delete(PRODUCT_PRICE_PREFIX + ":" + productId);
    }

}

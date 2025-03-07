package com.shop.shoppingapi.redis;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductPriceCacheRepositoryTest {

    @Autowired
    private ProductPriceCacheRepository productPriceCacheRepository;

    private final Long productId1 = 101L;
    private final Long productId2 = 202L;

    @BeforeEach
    public void setup() {
        // 테스트 시작 전 해당 상품의 캐시를 삭제해서 깨끗한 상태로 시작
        productPriceCacheRepository.deleteProductPrice(productId1);
        productPriceCacheRepository.deleteProductPrice(productId2);
    }

    @Test
    public void testSaveAndGetProductPrice() {
        // given: 원가격과 할인율 지정 (예: 20% 할인)
        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal discountRate = new BigDecimal("0.20");
        BigDecimal expectedDiscountedPrice = originalPrice.multiply(BigDecimal.ONE.subtract(discountRate));

        // when: 상품 가격 저장
        productPriceCacheRepository.saveProductPrice(productId1, originalPrice, discountRate);

        // then: 저장된 데이터를 조회하여 값 검증
        Map<Object, Object> priceData = productPriceCacheRepository.getProductPrice(productId1);
        assertNotNull(priceData);
        assertEquals(originalPrice.toString(), priceData.get("originalPrice"));
        assertEquals(discountRate.toString(), priceData.get("discountRate"));
        assertEquals(expectedDiscountedPrice.toString(), priceData.get("discountedPrice"));
        assertNotNull(priceData.get("lastUpdated"), "lastUpdated 값이 null이면 안됩니다.");
    }

    @Test
    public void testGetMultipleProductPrices() {
        // given: 두 개의 상품 가격 저장
        productPriceCacheRepository.saveProductPrice(productId1, new BigDecimal("200.00"), new BigDecimal("0.10"));
        productPriceCacheRepository.saveProductPrice(productId2, new BigDecimal("300.00"), new BigDecimal("0.15"));

        // when: 여러 상품 가격 조회
        Map<Long, Map<Object, Object>> multiplePrices =
                productPriceCacheRepository.getMultipleProductPrices(List.of(productId1, productId2));

        // then: 두 상품 모두 조회되어야 함
        assertEquals(2, multiplePrices.size());
        assertTrue(multiplePrices.containsKey(productId1));
        assertTrue(multiplePrices.containsKey(productId2));
    }

    @Test
    public void testDeleteProductPrice() {
        // given: 상품 가격 저장
        productPriceCacheRepository.saveProductPrice(productId1, new BigDecimal("150.00"), new BigDecimal("0.05"));

        // when: 상품 가격 삭제
        productPriceCacheRepository.deleteProductPrice(productId1);

        // then: 삭제 후 조회 결과는 빈 Map이어야 함
        Map<Object, Object> priceData = productPriceCacheRepository.getProductPrice(productId1);
        assertTrue(priceData.isEmpty());
    }
}

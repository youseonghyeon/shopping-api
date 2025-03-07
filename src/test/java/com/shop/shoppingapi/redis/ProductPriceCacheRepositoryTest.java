package com.shop.shoppingapi.redis;

import com.shop.shoppingapi.redis.dto.ProductPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductPriceCacheRepositoryTest {

    @Autowired
    private ProductPriceCacheRepository productPriceCacheRepository;

    private final Long productId1 = 101L;
    private final Long productId2 = 202L;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전, 기존 데이터 삭제
        productPriceCacheRepository.deleteById(productId1);
        productPriceCacheRepository.deleteById(productId2);
    }

    @Test
    public void testSaveAndFindById() {
        // given: ProductPrice 인스턴스 생성
        ProductPrice price = new ProductPrice(
                productId1,
                new BigDecimal("100.00"),
                0.20,
                new BigDecimal("80.00"),
                "Test Product",
                "test_image.jpg"
        );

        // when: 저장
        productPriceCacheRepository.save(price);

        // then: 단일 조회 후 값 검증
        Optional<ProductPrice> retrievedOpt = productPriceCacheRepository.findById(productId1);
        assertTrue(retrievedOpt.isPresent(), "저장된 상품 가격이 존재해야 합니다.");
        ProductPrice retrieved = retrievedOpt.get();
        assertEquals(price.getProductId(), retrieved.getProductId());
        assertEquals(price.getOriginalPrice(), retrieved.getOriginalPrice());
        assertEquals(price.getDiscountRate(), retrieved.getDiscountRate());
        assertEquals(price.getDiscountedPrice(), retrieved.getDiscountedPrice());
        assertEquals(price.getProductName(), retrieved.getProductName());
        assertEquals(price.getProductTitleImage(), retrieved.getProductTitleImage());
    }

    @Test
    public void testFindByIds() {
        // given: 두 개의 상품 가격 저장
        ProductPrice price1 = new ProductPrice(
                productId1,
                new BigDecimal("100.00"),
                0.20,
                new BigDecimal("80.00"),
                "Test Product 1",
                "image1.jpg"
        );
        ProductPrice price2 = new ProductPrice(
                productId2,
                new BigDecimal("200.00"),
                0.10,
                new BigDecimal("180.00"),
                "Test Product 2",
                "image2.jpg"
        );
        productPriceCacheRepository.save(price1);
        productPriceCacheRepository.save(price2);

        // when: 여러 상품 가격 조회
        Map<Long, ProductPrice> priceMap = productPriceCacheRepository.findByIds(java.util.List.of(productId1, productId2));

        // then: 두 상품이 모두 조회되어야 함
        assertEquals(2, priceMap.size(), "두 개의 상품 가격이 조회되어야 합니다.");
        assertTrue(priceMap.containsKey(productId1), "productId1의 상품 가격이 존재해야 합니다.");
        assertTrue(priceMap.containsKey(productId2), "productId2의 상품 가격이 존재해야 합니다.");
    }

    @Test
    public void testDeleteById() {
        // given: 상품 가격 저장
        ProductPrice price = new ProductPrice(
                productId1,
                new BigDecimal("100.00"),
                0.20,
                new BigDecimal("80.00"),
                "Test Product",
                "test_image.jpg"
        );
        productPriceCacheRepository.save(price);

        // 저장 확인
        Optional<ProductPrice> beforeDelete = productPriceCacheRepository.findById(productId1);
        assertTrue(beforeDelete.isPresent(), "삭제 전 상품 가격은 존재해야 합니다.");

        // when: 삭제
        productPriceCacheRepository.deleteById(productId1);

        // then: 삭제 후 조회 시 null이어야 함
        Optional<ProductPrice> afterDelete = productPriceCacheRepository.findById(productId1);
        assertFalse(afterDelete.isPresent(), "상품 가격이 삭제되어야 합니다.");
    }
}

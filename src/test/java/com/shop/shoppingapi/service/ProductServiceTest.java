package com.shop.shoppingapi.service;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.redis.SimpleProductCacheRepository;
import com.shop.shoppingapi.redis.dto.SimpleProduct;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.utils.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SimpleProductCacheRepository simpleProductCacheRepository;

    @Test
    @DisplayName("상품 생성 성공")
    void createProduct_Success() {
        // given
        Product product = ProductFixture.toProduct();
        // when
        long productId = productService.createProduct(product);
        // then
        Product savedProduct = productRepository.findById(productId).orElseThrow();
        assertNotNull(savedProduct);
        assertEquals(product.getName(), savedProduct.getName());
    }

    @Test
    @DisplayName("상품 조회 성공 (페이징)")
    void findProducts_Success() {
        // given
        super.save(ProductFixture.toProduct("상품1", null, null, null, null, null, null, null));
        super.save(ProductFixture.toProduct("상품2", null, null, null, null, null, null, null));
        super.save(ProductFixture.toProduct("테스트 상품", null, null, null, null, null, null, null));
        PageRequest pageable = PageRequest.of(0, 2);
        // when
        Page<Product> productsPage = productService.findProducts(pageable, "상품");
        // then
        assertEquals(2, productsPage.getContent().size());
        assertTrue(productsPage.getTotalElements() >= 3);
    }

    @Test
    @DisplayName("Redis 캐시가 없을 때 SimpleProduct 조회 성공")
    void findSimpleProductById_CacheMiss_Success() {
        // given
        Product product = super.save(ProductFixture.toProduct());
        // when
        SimpleProduct simpleProduct = productService.findSimpleProductById(product.getId());
        // then
        assertNotNull(simpleProduct);
        assertEquals(product.getId(), simpleProduct.getProductId());
        // cache 확인
        Optional<SimpleProduct> cached = simpleProductCacheRepository.findById(product.getId());
        assertTrue(cached.isEmpty(), "조회 후 자동 캐싱은 하지 않음");
    }

    @Test
    @DisplayName("Redis 캐시가 있을 때 SimpleProduct 조회 성공")
    void findSimpleProductById_CacheHit_Success() {
        // given
        Product product = super.save(ProductFixture.toProduct());
        SimpleProduct cachedProduct = ProductFixture.toSimpleProduct(product);
        simpleProductCacheRepository.save(cachedProduct);
        // when
        SimpleProduct simpleProduct = productService.findSimpleProductById(product.getId());
        // then
        assertNotNull(simpleProduct);
        assertEquals(cachedProduct.getProductId(), simpleProduct.getProductId());
    }

    @Test
    @DisplayName("여러 상품 조회 시 일부만 캐싱되어 있는 경우")
    void findSimpleProductsByIds_PartialCacheHit() {
        // given
        Product product1 = super.save(ProductFixture.toProduct("상품1", null, null, null, null, null, null, null));
        Product product2 = super.save(ProductFixture.toProduct("상품2", null, null, null, null, null, null, null));
        Product product3 = super.save(ProductFixture.toProduct("상품3", null, null, null, null, null, null, null));
        SimpleProduct cachedProduct = ProductFixture.toSimpleProduct(product1);
        simpleProductCacheRepository.save(cachedProduct);

        Set<Long> productIds = Set.of(product1.getId(), product2.getId(), product3.getId());

        // when
        Map<Long, SimpleProduct> simpleProducts = productService.findSimpleProductByIds(productIds);

        // then
        assertEquals(3, simpleProducts.size());
        assertTrue(simpleProducts.containsKey(product1.getId()));
        assertTrue(simpleProducts.containsKey(product2.getId()));
        assertTrue(simpleProducts.containsKey(product3.getId()));

        // 캐시 확인
        assertTrue(simpleProductCacheRepository.findById(product2.getId()).isPresent());
        assertTrue(simpleProductCacheRepository.findById(product3.getId()).isPresent());
    }
}

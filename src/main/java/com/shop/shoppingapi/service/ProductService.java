package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.ProductConverter;
import com.shop.shoppingapi.redis.SimpleProductCacheRepository;
import com.shop.shoppingapi.redis.dto.SimpleProduct;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.security.utils.LazyLoadingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SimpleProductCacheRepository simpleProductCacheRepository;

    @Transactional(readOnly = true)
    public Page<Product> findProducts(Pageable pageable, @Nullable String query) {
        return productRepository.findProductsInQueryDsl(pageable, query);
    }

    @Transactional(readOnly = true)
    public Page<Product> findProductsWithReviews(Pageable pageable, String query) {
        Page<Product> findProducts = productRepository.findProductsInQueryDsl(pageable, query);
        // Fetch reviews
        findProducts.forEach(product -> LazyLoadingUtils.forceLoad(product, Product::getReviews));
        return findProducts;
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findWithReviewsById(Long id) {
        return productRepository.findWithReviewsById(id);
    }

    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    public boolean existsProductById(Long productId) {
        return productRepository.existsById(productId);
    }

    public long createProduct(Product product) {
        return productRepository.save(product).getId();
    }

    public SimpleProduct findSimpleProductById(Long productId) {
        return simpleProductCacheRepository.findById(productId)
                .orElseGet(() -> {
                    log.debug("[Redis] Cache miss for productId: {}", productId);
                    return convertAndCacheSimpleProduct(productId);
                });
    }

    private SimpleProduct convertAndCacheSimpleProduct(Long productId) {
        return productRepository.findById(productId)
                .map(this::convertAndSaveSimpleProduct)
                .orElseThrow(() -> {
                    log.warn("[Product] Not found - productId: {}", productId);
                    return new IllegalArgumentException("Product not found: " + productId);
                });
    }

    public Map<Long, SimpleProduct> findSimpleProductByIds(Set<Long> productIds) {
        Map<Long, SimpleProduct> cachedSimpleProduct = simpleProductCacheRepository.findByIds(productIds);
        if (cachedSimpleProduct.size() == productIds.size()) {
            log.debug("[Redis] Cache hit");
            return cachedSimpleProduct;
        }
        log.debug("[Redis] cache miss: {}", productIds);

        List<Product> products = productRepository.findAllById(productIds);
        List<SimpleProduct> simpleProducts = products.stream().map(ProductConverter::toSimpleProduct).toList();
        simpleProducts.forEach(simpleProductCacheRepository::save);
        return simpleProducts.stream().collect(Collectors.toMap(SimpleProduct::getProductId, sp -> sp));
    }

    private SimpleProduct convertAndSaveSimpleProduct(Product product) {
        SimpleProduct simpleProduct = ProductConverter.toSimpleProduct(product);
        simpleProductCacheRepository.save(simpleProduct);
        log.debug("[Redis] Cached product - productId: {}", simpleProduct.getProductId());
        return simpleProduct;
    }

}

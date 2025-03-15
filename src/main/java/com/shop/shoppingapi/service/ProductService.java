package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.converter.ProductConverter;
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
        findProducts.forEach(product -> LazyLoadingUtils.forceLoad(product, Product::getReviews));
        return findProducts;
    }

    public boolean existsProduct(Long id) {
        return productRepository.existsById(id);
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

    public long createProduct(Product product) {
        return productRepository.save(product).getId();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateProduct(Long productId, Product productForUpdate) {
        findProductById(productId).ifPresent(product -> {
            // TODO: Implement update logic
            throw new UnsupportedOperationException("Not implemented yet");
        });
        // TODO: Implement update logic
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public SimpleProduct findSimpleProductById(Long productId) {
        Optional<SimpleProduct> redisSimpleProduct = simpleProductCacheRepository.findById(productId);
        if (redisSimpleProduct.isPresent()) {
            return redisSimpleProduct.get();
        }

        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            return ProductConverter.toSimpleProductFromEntity(product);
        }

        throw new IllegalArgumentException("Product not found");
    }

    public Map<Long, SimpleProduct> findSimpleProductByIds(List<Long> productIds) {
        Map<Long, SimpleProduct> cachedSimpleProduct = simpleProductCacheRepository.findByIds(productIds);
        if (cachedSimpleProduct.size() == productIds.size()) {
            return cachedSimpleProduct;
        }
        log.info("[Redis] cache miss: {}", productIds);

        List<Product> products = productRepository.findAllById(productIds);
        List<SimpleProduct> list = products.stream().map(ProductConverter::toSimpleProductFromEntity).toList();
        list.forEach(simpleProductCacheRepository::save);
        return list.stream().collect(Collectors.toMap(SimpleProduct::getProductId, pp -> pp));
    }

    public boolean existsProductById(Long productId) {
        return productRepository.existsById(productId);
    }
}

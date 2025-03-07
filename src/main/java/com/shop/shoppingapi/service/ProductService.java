package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.converter.ProductConverter;
import com.shop.shoppingapi.redis.ProductPriceCacheRepository;
import com.shop.shoppingapi.redis.dto.ProductPrice;
import com.shop.shoppingapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceCacheRepository productPriceCacheRepository;

    public Page<Product> findProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public boolean existsProduct(Long id) {
        return productRepository.existsById(id);
    }

    public Optional<Product> findProduct(Long id) {
        return productRepository.findById(id);
    }

    public long createProduct(Product product) {
        return productRepository.save(product).getId();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateProduct(Long productId, Product productForUpdate) {
        findProduct(productId).ifPresent(product -> {
            // TODO: Implement update logic
            throw new UnsupportedOperationException("Not implemented yet");
        });
        // TODO: Implement update logic
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ProductPrice findProductPriceById(Long productId) {
        Optional<ProductPrice> redisProductPrice = productPriceCacheRepository.findById(productId);
        if (redisProductPrice.isPresent()) {
            return redisProductPrice.get();
        }

        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            return ProductConverter.toProductPriceFromEntity(product);
        }

        throw new IllegalArgumentException("Product not found");
    }

    public Map<Long, ProductPrice> findProductPricesByIds(List<Long> productIds) {
        Map<Long, ProductPrice> cachedProductPrice = productPriceCacheRepository.findByIds(productIds);
        if (cachedProductPrice.size() == productIds.size()) {
            return cachedProductPrice;
        }
        log.info("[Redis] cache miss: {}", productIds);

        List<Product> products = productRepository.findAllById(productIds);
        List<ProductPrice> list = products.stream().map(ProductConverter::toProductPriceFromEntity).toList();
        list.forEach(productPriceCacheRepository::save);
        return list.stream().collect(Collectors.toMap(ProductPrice::getProductId, pp -> pp));
    }

}

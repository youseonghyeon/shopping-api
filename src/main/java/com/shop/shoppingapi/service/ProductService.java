package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
           throw new UnsupportedOperationException("Not implemented yet");
        });
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

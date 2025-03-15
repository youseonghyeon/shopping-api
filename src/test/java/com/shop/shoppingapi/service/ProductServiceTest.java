package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.ProductConverterForTest;
import com.shop.shoppingapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindProductsById() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.findProducts(pageable, null);

        assertEquals(productPage, result);
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void testExistsProduct() {
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);

        boolean exists = productService.existsProduct(productId);

        assertTrue(exists);
        verify(productRepository, times(1)).existsById(productId);
    }

    @Test
    void testFindProductById() {
        Long productId = 1L;
        Product product = ProductConverterForTest.toEntity(productId, "Test Product", "http://example.com/image.jpg", "Test Title", BigDecimal.valueOf(99.99), "Test Description", "Test Category", 100);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findProductById(productId);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testCreateProduct() {
        Product product = ProductConverterForTest.toEntity(null, "Test Product", "http://example.com/image.jpg", "Test Title", BigDecimal.valueOf(99.99), "Test Description", "Test Category", 100);
        Product savedProduct = ProductConverterForTest.toEntity(1L, "Test Product", "http://example.com/image.jpg", "Test Title", BigDecimal.valueOf(99.99), "Test Description", "Test Category", 100);
        when(productRepository.save(product)).thenReturn(savedProduct);

        long productId = productService.createProduct(product);

        assertEquals(1L, productId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testDeleteProduct() {
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void testUpdateProduct() {
        Long productId = 1L;
        Product productForUpdate = ProductConverterForTest.toEntity(null, "Updated Product", "http://example.com/image.jpg", "Updated Title", BigDecimal.valueOf(199.99), "Updated Description", "Updated Category", 200);
        when(productRepository.findById(productId)).thenReturn(Optional.of(ProductConverterForTest.toEmptyEntity()));

        assertThrows(UnsupportedOperationException.class, () -> {
            productService.updateProduct(productId, productForUpdate);
        });

        verify(productRepository, times(1)).findById(productId);
    }
}

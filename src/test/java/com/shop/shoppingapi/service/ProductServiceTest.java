package com.shop.shoppingapi.service;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ProductServiceTest extends IntegrationTestSupport {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
    void testDeleteProduct() {
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

}

package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.CreateProductRequest;
import com.shop.shoppingapi.controller.dto.DeleteProductRequest;
import com.shop.shoppingapi.controller.dto.ProductResponse;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.ProductConverter;
import com.shop.shoppingapi.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        Optional<Product> product = productService.findProduct(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product findProduct = product.get();
        ProductResponse productResponse = new ProductResponse(findProduct.getId(), findProduct.getName(), findProduct.getTitleImage(), findProduct.getTitle(), findProduct.getPrice());
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/products")
    public Page<Product> getProducts(Pageable pageable) {
        return productService.findProducts(pageable);
    }


    @PostMapping("/products/create")
    public ResponseEntity<Object> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        Product entity = ProductConverter.toEntity(createProductRequest);
        long productId = productService.createProduct(entity);
        log.info("Created product with id {}", productId);
        return ResponseEntity.ok(productId);
    }

    @PostMapping("/products/delete")
    public ResponseEntity<Object> deleteProduct(@RequestBody DeleteProductRequest deleteProductRequest) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/products/{id}/reviews")
    public String createReview(@PathVariable String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

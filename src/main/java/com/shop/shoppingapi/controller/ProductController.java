package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/products")
    public Page<Product> getProducts(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/products/{id}/reviews")
    public String getReviews(@PathVariable String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/products")
    public String createProduct() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/products/{id}/reviews")
    public String createReview(@PathVariable String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

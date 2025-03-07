package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.*;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.converter.ProductConverter;
import com.shop.shoppingapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final CustomPagedModelAssembler<ProductResponse> pagedModelAssembler;


    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        Optional<Product> product = productService.findProduct(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product findProduct = product.get();
        ProductResponse productResponse = new ProductResponse(findProduct.getId(), findProduct.getName(), findProduct.getTitleImage(), findProduct.getTitle(), findProduct.getPrice());
        return ApiResponse.success(productResponse);
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PagedModel<ProductResponse>>> getProducts(@PageableDefault(size = 10) Pageable pageable) {
        Page<Product> products = productService.findProducts(pageable);
        Page<ProductResponse> productsResponse = products.map(ProductResponse::of);
        return ApiResponse.success(pagedModelAssembler.toModel(productsResponse));
    }


    @PostMapping("/products/create")
    public ResponseEntity<ApiResponse<Long>> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        Product entity = ProductConverter.toEntity(createProductRequest);
        long productId = productService.createProduct(entity);
        log.info("Created product with id {}", productId);
        return ApiResponse.success(productId, "상품 등록을 성공하였습니다.");
    }

    @PostMapping("/products/delete")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@RequestBody DeleteProductRequest deleteProductRequest) {
        log.error("Not implemented yet");
        return ApiResponse.error("Not implemented yet", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/products/{id}/reviews")
    public ResponseEntity<ApiResponse<Void>> createReview(@PathVariable String id) {
        log.error("Not implemented yet");
        return ApiResponse.error("Not implemented yet", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

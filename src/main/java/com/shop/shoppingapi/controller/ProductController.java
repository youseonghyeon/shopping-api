package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CustomPagedModelAssembler;
import com.shop.shoppingapi.controller.dto.product.CreateProductRequest;
import com.shop.shoppingapi.controller.dto.product.DeleteProductRequest;
import com.shop.shoppingapi.controller.dto.product.ProductResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final CustomPagedModelAssembler<ProductResponse> pagedModelAssembler;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PagedModel<ProductResponse>>> getProducts(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String query) {
        Page<Product> products = productService.findProductsWithReviews(pageable, query);
        Page<ProductResponse> productsResponse = products.map(product -> ProductResponse.from(product, true));
        return ApiResponse.success(pagedModelAssembler.toModel(productsResponse));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        Optional<Product> product = productService.findWithReviewsById(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ProductResponse productResponse = ProductResponse.from(product.get(), true);
        return ApiResponse.success(productResponse);
    }

    @GetMapping("/products/ids")
    public ResponseEntity<? extends ApiResponse<?>> getProductsByIds(@RequestParam("productIds") List<Long> productIds) {
        List<Product> findProducts = productService.findProductsByIds(productIds);
        if (findProducts.isEmpty()) {
            return ApiResponse.error("상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> list = findProducts.stream().map(ProductResponse::from).toList();
        return ApiResponse.success(list);
    }

    @GetMapping("/products/details")
    public ResponseEntity<? extends ApiResponse<?>> getProductDetails(@RequestParam List<Long> productIds) {
        List<Product> findProducts = productService.findProductsByIds(productIds);
        if (findProducts.isEmpty()) {
            return ApiResponse.error("상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        List<ProductResponse> list = findProducts.stream().map(ProductResponse::from).toList();
        return ApiResponse.success(list);
    }

    @PostMapping("/products/create")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<Long>> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        Product entity = ProductConverter.toEntity(createProductRequest);
        long productId = productService.createProduct(entity);
        log.info("Created product with id {}", productId);
        return ApiResponse.success(productId, "상품 등록을 성공하였습니다.");
    }

    @PostMapping("/products/delete")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@RequestBody DeleteProductRequest deleteProductRequest) {
        log.error("Not implemented yet");
        return ApiResponse.error("Not implemented yet", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/products/{id}/reviews")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<Void>> createReview(@PathVariable String id) {
        log.error("Not implemented yet");
        return ApiResponse.error("Not implemented yet", HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.controller.dto.CustomPagedModelAssembler;
import com.shop.shoppingapi.controller.dto.product.ProductResponse;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final ProductService productService;
    private final CustomPagedModelAssembler<ProductResponse> pagedModelAssembler;

    @GetMapping("/search/products")
    public ResponseEntity<ApiResponse<PagedModel<ProductResponse>>> searchProducts(@PageableDefault(size = 10) Pageable pageable, @RequestParam("q") String query) {
        Page<Product> products = productService.findProducts(pageable, query);
        Page<ProductResponse> productsResponse = products.map(ProductResponse::of);
        return ApiResponse.success(pagedModelAssembler.toModel(productsResponse));
    }
}

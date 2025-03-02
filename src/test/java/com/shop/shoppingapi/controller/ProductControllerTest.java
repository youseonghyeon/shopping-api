package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void getProduct() throws Exception {
        mockMvc.perform(get("/products/{id}", "1"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    @DisplayName("상품 목록 조회 테스트")
    void getProducts() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(productService.findProducts(pageable)).thenReturn(products);

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("상품 리뷰 목록 조회 테스트")
    void getReviews() throws Exception {
        mockMvc.perform(get("/products/{id}/reviews", "1"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    @DisplayName("상품 생성 테스트")
    void createProduct() throws Exception {
        mockMvc.perform(post("/products/create"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    void createReview() throws Exception {
        mockMvc.perform(post("/products/{id}/reviews", "1"))
                .andExpect(status().isNotImplemented());
    }
}

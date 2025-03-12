package com.shop.shoppingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.controller.dto.CustomPagedModelAssembler;
import com.shop.shoppingapi.controller.dto.product.CreateProductRequest;
import com.shop.shoppingapi.controller.dto.product.ProductResponse;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.TestUtils;
import com.shop.shoppingapi.entity.converter.ProductConverter;
import com.shop.shoppingapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    // CustomPagedModelAssembler<ProductResponse> Mock
    @Mock
    private CustomPagedModelAssembler<ProductResponse> pagedModelAssembler;

    private ProductController productController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productController = new ProductController(productService, pagedModelAssembler);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getProducts_success() throws Exception {
        // given: Pageable 및 테스트용 Product 데이터 준비
        Pageable pageable = PageRequest.of(0, 10);
        Product product1 = TestUtils.createProduct(1L, "상품1", "image1", "title1", new BigDecimal("1000"));
        Product product2 = TestUtils.createProduct(2L, "상품2", "image2", "title2", new BigDecimal("2000"));
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1, product2), pageable, 2);
        when(productService.findProducts(any(Pageable.class))).thenReturn(productPage);

        // Response Page<ProductResponse> 생성
        Page<ProductResponse> responsePage = productPage.map(ProductResponse::of);
        PagedModel<ProductResponse> pagedModel = PagedModel.of(
                responsePage.getContent(),
                new PagedModel.PageMetadata(responsePage.getSize(), responsePage.getNumber(), responsePage.getTotalElements())
        );
        when(pagedModelAssembler.toModel(any(Page.class))).thenReturn(pagedModel);

        // when & then: 응답 JSON 검증
        mockMvc.perform(get("/api/products").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                // data 필드가 null이 아님을 확인
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data._embedded.productResponseList").isArray())
                .andExpect(jsonPath("$.data._embedded.productResponseList[0].productId").value(1))
                .andExpect(jsonPath("$.data._embedded.productResponseList[0].productName").value("상품1"))
                .andExpect(jsonPath("$.data._embedded.productResponseList[1].productId").value(2))
                .andExpect(jsonPath("$.data.page.totalElements").value(2));
    }

    @Test
    void getProduct_success() throws Exception {
        Long productId = 1L;
        Product product = TestUtils.createProduct(productId, "상품1", "image1", "title1", new BigDecimal("1000"));
        when(productService.findProductById(productId)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(productId))
                .andExpect(jsonPath("$.data.productName").value("상품1"));
    }

    @Test
    void getProduct_notFound() throws Exception {
        Long productId = 1L;
        when(productService.findProductById(productId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductDetails_success() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L);
        Product product1 = TestUtils.createProduct(1L, "상품1", "image1", "title1", new BigDecimal("1000"));
        Product product2 = TestUtils.createProduct(2L, "상품2", "image2", "title2", new BigDecimal("2000"));
        when(productService.findProductsByIds(productIds)).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products/details")
                        .param("productIds", "1", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].productId").value(1))
                .andExpect(jsonPath("$.data[1].productId").value(2));
    }

    @Test
    void getProductDetails_notFound() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L);
        when(productService.findProductsByIds(productIds)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products/details")
                        .param("productIds", "1", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_success() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        // 테스트용 요청 값 설정 (필드 이름은 실제 DTO와 일치하도록 조정)
        request.setName("상품1");
        request.setTitleImage("image1");
        request.setTitle("title1");
        request.setPrice(new BigDecimal("1000"));

        // Product 엔티티로 변환
        Product product = ProductConverter.toEntity(request);
        when(productService.createProduct(any(Product.class))).thenReturn(1L);

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1))
                .andExpect(jsonPath("$.message").value("상품 등록을 성공하였습니다."));
    }


    @Test
    void createReview_notImplemented() throws Exception {
        String productId = "1";

        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Not implemented yet"));
    }

}

package com.shop.shoppingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.controller.dto.cart.CartResponse;
import com.shop.shoppingapi.controller.dto.cart.CreateCartRequest;
import com.shop.shoppingapi.controller.dto.cart.DeleteCartRequest;
import com.shop.shoppingapi.controller.dto.cart.UpdateCartRequest;
import com.shop.shoppingapi.entity.UserTestUtils;
import com.shop.shoppingapi.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    private CartController cartController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
        cartController = new CartController(cartService); // Mock 객체 주입
        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();

        // 로그인 사용자 설정
        UserTestUtils.login(1L, "testUser", "testPassword", "test@mail.com", "01012345678");
    }

    @Test
    void 장바구니_목록_조회() throws Exception {
        Long userId = 1L;
        List<CartResponse> mockCartResponses = Arrays.asList(
                new CartResponse(1L, 2, new BigDecimal("1000"), 0.1, new BigDecimal("900"), "상품1", "image1"),
                new CartResponse(2L, 1, new BigDecimal("2000"), 0.2, new BigDecimal("1600"), "상품2", "image2")
        );

        when(cartService.findCarts(userId)).thenReturn(mockCartResponses);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].productId").value(1L))
                .andExpect(jsonPath("$.data[0].productName").value("상품1"))
                .andExpect(jsonPath("$.data[1].productId").value(2L));
    }

    @Test
    void 장바구니_목록_조회_빈리스트() throws Exception {
        Long userId = 1L;
        when(cartService.findCarts(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 장바구니_상품_추가() throws Exception {
        Long userId = 1L;
        CreateCartRequest request = new CreateCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        doNothing().when(cartService).addCartItem(userId, request.getProductId(), request.getQuantity());

        mockMvc.perform(post("/api/cart/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("장바구니에 상품을 추가하였습니다."));
    }

    @Test
    void 장바구니_상품_추가_실패_없는상품() throws Exception {
        CreateCartRequest request = new CreateCartRequest();
        request.setProductId(9999L);
        request.setQuantity(2);

        doThrow(new IllegalArgumentException("존재하지 않는 상품입니다.")).when(cartService).addCartItem(any(Long.class), any(Long.class), anyInt());

        mockMvc.perform(post("/api/cart/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."));

    }

    @Test
    void 장바구니_상품_삭제() throws Exception {
        Long userId = 1L;
        DeleteCartRequest request = new DeleteCartRequest();
        request.getProductIds().add(1L);
        request.getProductIds().add(2L);

        doNothing().when(cartService).deleteCartItem(userId, request.getProductIds());

        mockMvc.perform(post("/api/cart/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("장바구니에서 상품을 삭제하였습니다."));
    }

    @Test
    void 장바구니_상품_삭제_성공_빈목록() throws Exception {
        DeleteCartRequest request = new DeleteCartRequest();

        mockMvc.perform(post("/api/cart/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 장바구니_상품_수정() throws Exception {
        Long userId = 1L;
        UpdateCartRequest request = new UpdateCartRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        doNothing().when(cartService).updateCartItem(userId, request.getProductId(), request.getQuantity());

        mockMvc.perform(post("/api/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("장바구니에 상품을 수정하였습니다."));
    }

    @Test
    void 장바구니_상품_수정_실패_수량이_0이하() throws Exception {
        UpdateCartRequest request = new UpdateCartRequest();
        request.setProductId(1L);
        request.setQuantity(0);

        mockMvc.perform(post("/api/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 비로그인_사용자_장바구니_접근_실패() throws Exception {
        SecurityContextHolder.clearContext(); // 인증 정보 제거

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }
}

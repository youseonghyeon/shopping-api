package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.redis.CartCacheRepository;
import com.shop.shoppingapi.repository.OrderRepository;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // 불필요한 stubbing 경고 완화
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartCacheRepository cartCacheRepository;

    @Mock
    private OrderValidationService orderValidationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("submitOrder - 성공: 주문 제출 및 관련 데이터 업데이트")
    void submitOrder_success() {
        // given
        Long userId = 1L;
        Long productId1 = 100L;
        Long productId2 = 101L;

        // SubmitOrderRequest에 필요한 값들을 설정
        SubmitOrderRequest request = new SubmitOrderRequest();
        // 예시: 요청 상품 ID 리스트, 주문 항목, 사용 포인트 등을 설정
        SubmitOrderRequest.OrderItemRequest orderItemRequest1 = new SubmitOrderRequest.OrderItemRequest();
        orderItemRequest1.setProductId(productId1);
        orderItemRequest1.setPrice(BigDecimal.valueOf(10000));
        orderItemRequest1.setQuantity(100);
        SubmitOrderRequest.OrderItemRequest orderItemRequest2 = new SubmitOrderRequest.OrderItemRequest();
        orderItemRequest2.setProductId(productId2);
        orderItemRequest2.setPrice(BigDecimal.valueOf(10000));
        orderItemRequest2.setQuantity(100);
        request.getItems().addAll(Arrays.asList(orderItemRequest1, orderItemRequest2));
        // 필요에 따라 request.setItems(...) 등 추가
        request.setUsedPoints(100);
        request.setPaymentMethod("CARD");
        // ShippingInfo 필수값 설정 (예: 주소, 수령인, 전화번호)
        SubmitOrderRequest.ShippingInfo shippingInfo = new SubmitOrderRequest.ShippingInfo();
        shippingInfo.setAddress("서울시 강남구");
        shippingInfo.setRecipientName("홍길동");
        shippingInfo.setPhone("01012345678");
        request.setShippingInfo(shippingInfo);
        request.setTotalPayment(BigDecimal.valueOf(10000));
        request.setTotalProductPrice(BigDecimal.valueOf(10000));
        request.setShippingFee(BigDecimal.valueOf(10000));
        request.setDiscountSum(BigDecimal.valueOf(10000));
        // 사용자 모킹: User 생성 대신 mock 사용 (생성자 및 setter가 protected여서)
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // 상품 모킹: productRepository.findAllById()에서 사용
        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);
        when(productRepository.findAllById(anyList()))
                .thenReturn(Arrays.asList(product1, product2));

        // orderValidationService 검증: 예외 없이 진행
        doNothing().when(orderValidationService).validateOrder(request);
        doNothing().when(orderValidationService).validateUserPoint(eq(user), any());

        // Order 모킹: orderRepository.save() 호출 시 반환값 설정
        Order savedOrder = mock(Order.class);
        when(savedOrder.getId()).thenReturn(500L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // cartCacheRepository 삭제 처리 모킹
        doNothing().when(cartCacheRepository).deleteCartItems(eq(userId), anyList());

        // when
        Long resultOrderId = orderService.submitOrder(userId, request);

        // then
        assertEquals(500L, resultOrderId);
        verify(orderValidationService).validateOrder(request);
        verify(orderValidationService).validateUserPoint(eq(user), any());
        verify(userRepository).save(user);
        verify(cartCacheRepository).deleteCartItems(eq(userId), anyList());
    }

    @Test
    @DisplayName("submitOrder - 실패: 사용자 정보가 없으면 예외 발생")
    void submitOrder_failure_noUser() {
        // given
        Long userId = 1L;
        SubmitOrderRequest request = new SubmitOrderRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                orderService.submitOrder(userId, request)
        );
        assertEquals("사용자 정보가 없습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("findOrderById - 성공: 주문 조회")
    void findOrderById_success() {
        // given
        Long orderId = 500L;
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        Order foundOrder = orderService.findOrderById(orderId);

        // then
        assertNotNull(foundOrder);
        assertEquals(orderId, foundOrder.getId());
    }

    @Test
    @DisplayName("findOrderById - 실패: 주문 정보가 없으면 예외 발생")
    void findOrderById_failure() {
        // given
        Long orderId = 500L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                orderService.findOrderById(orderId)
        );
        assertEquals("주문 정보가 없습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("findOrdersWithOrdersItemsAndProductByUserId - 성공: 사용자 주문 목록 조회")
    void findOrdersWithOrdersItemsAndProductByUserId_success() {
        // given
        Long userId = 1L;
        Pageable pageable = Pageable.unpaged();

        // 모킹 Order 객체들
        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);
        lenient().when(order1.getId()).thenReturn(500L);
        lenient().when(order2.getId()).thenReturn(501L);
        List<Order> orders = Arrays.asList(order1, order2);
        Page<Order> pagedOrders = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findAllByBuyerId(userId, pageable)).thenReturn(pagedOrders);

        // when
        Page<Order> result = orderService.findOrdersWithOrdersItemsAndProductByUserId(userId, pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
}

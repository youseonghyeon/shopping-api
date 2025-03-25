package com.shop.shoppingapi.service;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.OrderItem;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.repository.OrderRepository;
import com.shop.shoppingapi.utils.OrderFixture;
import com.shop.shoppingapi.utils.OrderItemFixture;
import com.shop.shoppingapi.utils.ProductFixture;
import com.shop.shoppingapi.utils.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("주문 제출 성공")
    void submitOrder_Success() {
        // given
        User user = super.save(UserFixture.toUser());
        SubmitOrderRequest request = OrderFixture.toSubmitOrderRequest();

        // when
        Long orderId = orderService.submitOrder(user.getId(), request);

        // then
        Order savedOrder = orderRepository.findById(orderId).orElse(null);

        assertNotNull(savedOrder);
        assertEquals(user.getId(), savedOrder.getBuyer().getId());
        assertEquals(request.getUsedPoints(), savedOrder.getUsedPoints());
        assertEquals(request.getItems().size(), savedOrder.getOrderItems().size());
    }

    @Test
    @DisplayName("주문 제출 실패: 사용자 포인트 부족")
    void submitOrder_Fail_InsufficientPoints() {
        // given
        User user = super.save(UserFixture.toUser(null, null, null, null, 10000, null, null));
        Product product = super.save(ProductFixture.toProduct(null, null, null, BigDecimal.valueOf(100_000), null, null, null, 0.0));

        SubmitOrderRequest request = OrderFixture.toSubmitOrderRequest(null, null, 20000, BigDecimal.valueOf(100_000), BigDecimal.valueOf(3_000), BigDecimal.ZERO, BigDecimal.valueOf(83_000), product);

        // when, then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> orderService.submitOrder(user.getId(), request), "사용 가능한 포인트가 부족합니다.");
        assertEquals("사용 가능한 포인트가 부족합니다.", illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("주문 조회 성공")
    void findOrderById_Success() {
        // given
        User user = super.save(UserFixture.toUser());
        Product product = super.save(ProductFixture.toProduct());
        OrderItem orderItem = super.save(OrderItemFixture.toOrderItem(product));
        Order order = super.save(OrderFixture.toOrder(user, orderItem));

        SubmitOrderRequest request = OrderFixture.toSubmitOrderRequest();
        Long orderId = orderService.submitOrder(user.getId(), request);

        // when
        Order findOrder = orderService.findOrderById(orderId);

        // then
        assertNotNull(findOrder);
        assertEquals(orderId, findOrder.getId());
    }
}

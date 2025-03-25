package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.IntegrationTestSupport;
import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.entity.*;
import com.shop.shoppingapi.utils.OrderFixture;
import com.shop.shoppingapi.utils.OrderItemFixture;
import com.shop.shoppingapi.utils.ProductFixture;
import com.shop.shoppingapi.utils.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@TestPropertySource(properties = {
//        "spring.jpa.hibernate.ddl-auto=create-drop"
//})
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("구매자로 주문 조회 성공")
    void findAllByBuyerId() {
        // given
        User user = super.save(UserFixture.toUser());
        Product product = super.save(ProductFixture.toProduct());
        OrderItem orderItem1 = super.save(OrderItemFixture.toOrderItem(product));
        OrderItem orderItem2 = super.save(OrderItemFixture.toOrderItem(product));
        Order order1 = super.save(OrderFixture.toOrder(user, orderItem1));
        Order order2 = super.save(OrderFixture.toOrder(user, orderItem2));
        // when
        Page<Order> orders = orderRepository.findAllByBuyerId(user.getId(), PageRequest.of(0, 10));
        // then
        assertEquals(2, orders.getTotalElements());
        List<Order> content = orders.getContent();
        Set<Long> idCollections = content.stream().map(Order::getId).collect(Collectors.toSet());
        assertTrue(idCollections.contains(order1.getId()));
        assertTrue(idCollections.contains(order2.getId()));
    }


}

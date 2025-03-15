package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.converter.UserConverter;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("findAllByBuyerId - 구매자 ID에 해당하는 주문들이 페이지로 조회되어야 한다.")
    void findAllByBuyerId_success() {
        // given: 테스트용 User 엔티티 생성 및 저장 using UserConverter와 CreateUserRequest의 생성자
        CreateUserRequest request1 = new CreateUserRequest(
                "testuser@example.com",
                "testuser",
                "password123",
                "password123",
                "01011112222"
        );
        User buyer1 = UserConverter.toEntity(request1, "dummyPassword");
        buyer1 = em.persistAndFlush(buyer1);
        Long buyer1Id = buyer1.getId();

        CreateUserRequest request2 = new CreateUserRequest(
                "another@example.com",
                "anotherUser",
                "password123",
                "password123",
                "01033334444"
        );
        User buyer2 = UserConverter.toEntity(request2, "dummyPassword");
        buyer2 = em.persistAndFlush(buyer2);

        // Order 엔티티 생성 (필수 필드를 모두 채워 Order.builder()로 생성)
        Order order1 = Order.builder()
                .orderNumber("ORD-001")
                .buyer(buyer1)
                .recipientName("홍길동")
                .address("서울시 강남구")
                .phone("01012345678")
                .paymentMethod("card")
                .usedPoints(0)
                .totalProductPrice(new BigDecimal("1000.00"))
                .shippingFee(new BigDecimal("100.00"))
                .discountSum(new BigDecimal("0.00"))
                .finalPayment(new BigDecimal("1100.00"))
                .orderStatus("completed")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order order2 = Order.builder()
                .orderNumber("ORD-002")
                .buyer(buyer1)
                .recipientName("홍길동")
                .address("서울시 강남구")
                .phone("01012345678")
                .paymentMethod("card")
                .usedPoints(0)
                .totalProductPrice(new BigDecimal("2000.00"))
                .shippingFee(new BigDecimal("150.00"))
                .discountSum(new BigDecimal("100.00"))
                .finalPayment(new BigDecimal("2050.00"))
                .orderStatus("completed")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order order3 = Order.builder()
                .orderNumber("ORD-003")
                .buyer(buyer2)
                .recipientName("김철수")
                .address("부산시 해운대구")
                .phone("01098765432")
                .paymentMethod("card")
                .usedPoints(0)
                .totalProductPrice(new BigDecimal("3000.00"))
                .shippingFee(new BigDecimal("200.00"))
                .discountSum(new BigDecimal("50.00"))
                .finalPayment(new BigDecimal("3150.00"))
                .orderStatus("completed")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        em.persist(order1);
        em.persist(order2);
        em.persist(order3);
        em.flush();

        // when: buyer1의 주문 목록 조회 (페이지 사이즈 10)
        Page<Order> page = orderRepository.findAllByBuyerId(buyer1.getId(), PageRequest.of(0, 10));

        // then
        assertNotNull(page, "페이지 결과는 null이 아니어야 한다.");
        assertEquals(2, page.getTotalElements(), "구매자 ID가 buyer1인 주문은 2건이어야 한다.");
        page.getContent().forEach(order ->
                assertEquals(buyer1Id, order.getBuyer().getId(), "모든 주문의 buyerId는 buyer1이어야 한다.")
        );
    }
}

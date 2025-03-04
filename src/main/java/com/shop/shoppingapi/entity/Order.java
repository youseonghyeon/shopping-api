package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문 번호 (고유 식별자)
    @Column(nullable = false, unique = true)
    private String orderNumber;

    // 주문 날짜 및 시간
    @Column(nullable = false)
    private LocalDateTime orderDate;

    // 주문 상태 (예: 주문완료, 배송중, 배송완료 등)
    @Column(nullable = false)
    private String status;

    // 총 주문 금액
    @Column(nullable = false)
    private Double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 배송 주소
    private String shippingAddress;
}

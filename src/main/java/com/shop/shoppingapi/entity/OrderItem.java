package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_rate", nullable = false, precision = 5)
    private Double discountRate;

    @Column(name = "discounted_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountedPrice; // 할인 적용 후 단가 (unitPrice * (1 - discountRate))

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice; // 할인 적용 후 단가 * quantity
}

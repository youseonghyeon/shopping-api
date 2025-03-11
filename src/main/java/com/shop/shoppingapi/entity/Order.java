package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    private String orderNumber; // 고유 주문 번호

    @JoinColumn(name = "buyer_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User buyer; // 주문한 고객의 ID

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName; // 수령인 이름

    @Column(name = "address", nullable = false, length = 255)
    private String address; // 배송 주소

    @Column(name = "phone", nullable = false, length = 20)
    private String phone; // 연락처

    @Column(name = "delivery_request", length = 255)
    private String deliveryRequest; // 배송 요청사항

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod; // 결제 수단 (bank, card, mobile)

    @Column(name = "used_points", nullable = false)
    private Integer usedPoints; // 사용한 포인트

    @Column(name = "total_product_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalProductPrice; // 할인 적용 후 상품 총액

    @Column(name = "shipping_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingFee; // 배송비

    @Column(name = "discount_sum", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountSum; // 총 할인 금액

    @Column(name = "final_payment", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPayment; // 최종 결제 금액 (totalProductPrice - usedPoints + shippingFee)

    @Column(name = "order_status", nullable = false, length = 20)
    private String orderStatus; // 주문 상태 (예: pending, completed, cancelled)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 주문 생성 일시

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 마지막 수정 일시

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems; // 주문 아이템 리스트

    public void clearOrderItems() {
        this.orderItems = new ArrayList<>();
    }

    public void addOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}

package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.OrderItem;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.redis.CartCacheRepository;
import com.shop.shoppingapi.repository.OrderRepository;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartCacheRepository cartCacheRepository;

    @Transactional
    public Long submitOrder(Long userId, SubmitOrderRequest request) {
        // 1. 요청된 상품이 모두 존재하는지 검증
        List<Long> productIds = request.getProductIds();
        List<Product> products = productRepository.findAllById(productIds);
        validateProductsExist(products, productIds);

        // 2. 상품 가격 맵 구성 (상품ID -> 원래 가격)
        Map<Long, BigDecimal> discountedPriceMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getDiscountedPrice));

        // 3. 상품별 할인율을 적용하여 총 상품 가격 계산
        BigDecimal calculatedItemsTotal = calculateItemsTotal(request.getItems(), discountedPriceMap);
        validateTotalProductPrice(calculatedItemsTotal, request.getTotalProductPrice());

        // 4. 원래 가격 총합과 할인 총액 검증
        BigDecimal sumOriginalPrices = calculateOriginalSum(request.getItems(), discountedPriceMap);
        BigDecimal expectedDiscountSum = sumOriginalPrices.subtract(calculatedItemsTotal);
        validateDiscountSum(expectedDiscountSum, request.getDiscountSum());

        // 5. 최종 결제 금액 검증: (할인 후 상품총액 - usedPoints) + shippingFee
        BigDecimal shippingFee = request.getShippingFee();
        BigDecimal usedPoints = new BigDecimal(request.getUsedPoints());
        BigDecimal expectedFinalPayment = calculatedItemsTotal.subtract(usedPoints).add(shippingFee);
        validateFinalPayment(expectedFinalPayment, request.getTotalPayment());

        // 6. 사용자 포인트 검증 및 차감
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
        if (user.getPoint() < request.getUsedPoints()) {
            throw new IllegalArgumentException("사용 가능한 포인트가 부족합니다.");
        }

        // 7. 주문 번호 생성
        String orderNumber = generateOrderNumber();

        // 8. 주문 헤더(Order) 엔티티 생성
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerId(userId)
                .recipientName(request.getShippingInfo().getRecipientName())
                .address(request.getShippingInfo().getAddress())
                .phone(request.getShippingInfo().getPhone())
                .deliveryRequest(request.getShippingInfo().getDeliveryRequest())
                .paymentMethod(request.getPaymentMethod())
                .usedPoints(request.getUsedPoints())
                .totalProductPrice(request.getTotalProductPrice())
                .shippingFee(request.getShippingFee())
                .discountSum(request.getDiscountSum())
                .finalPayment(request.getTotalPayment())
                .orderStatus("pending")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        order.clearOrderItems();

        // 9. 주문 아이템(OrderItem) 생성 (할인율 10% 적용 예시)
        BigDecimal discountRate = new BigDecimal("0.10");
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountRate); // 0.9
        for (SubmitOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            // 요청된 price는 할인 적용 후 가격이므로, 원래 단가 = discountedPrice / discountMultiplier
            BigDecimal discountedPrice = itemReq.getPrice();
            BigDecimal unitPrice = discountedPrice.divide(discountMultiplier, 2, RoundingMode.HALF_UP);
            BigDecimal totalPrice = discountedPrice.multiply(new BigDecimal(itemReq.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(itemReq.getProductId())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .discountRate(discountRate)
                    .discountedPrice(discountedPrice)
                    .totalPrice(totalPrice)
                    .build();
            order.getOrderItems().add(orderItem);
        }

        // 10. 주문 저장 (OrderItem은 CascadeType.ALL로 함께 저장)
        Order savedOrder = orderRepository.save(order);

        // 11. 사용자 포인트 차감
        user.usePoints(request.getUsedPoints());
        userRepository.save(user);

        // 12. 장바구니 삭제 처리 (해당 상품들을 사용자 장바구니에서 삭제)
        cartCacheRepository.deleteCartItems(userId, productIds);

        return savedOrder.getId();
    }

    private void validateProductsExist(List<Product> products, List<Long> productIds) {
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("상품 정보가 올바르지 않습니다.");
        }
    }

    private BigDecimal calculateItemsTotal(List<SubmitOrderRequest.OrderItemRequest> items, Map<Long, BigDecimal> productPriceMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (SubmitOrderRequest.OrderItemRequest item : items) {
            Long productId = item.getProductId();
            BigDecimal discountedPrice = productPriceMap.get(productId);
            if (discountedPrice == null) {
                throw new IllegalArgumentException("상품 정보 누락: " + productId);
            }
            // 예상 총액 = 할인 적용 후 단가 * 수량
            BigDecimal expectedTotalPrice = discountedPrice.multiply(new BigDecimal(item.getQuantity()));
            BigDecimal multiply = discountedPrice.multiply(new BigDecimal(item.getQuantity()));
            if (multiply.compareTo(expectedTotalPrice) != 0) {
                throw new IllegalArgumentException("상품 ID " + productId + "의 주문 금액이 올바르지 않습니다.");
            }
            total = total.add(expectedTotalPrice);
        }
        return total;
    }

    private void validateTotalProductPrice(BigDecimal calculatedTotal, BigDecimal requestTotal) {
        if (calculatedTotal.compareTo(requestTotal) != 0) {
            throw new IllegalArgumentException("총 상품 가격이 올바르지 않습니다.");
        }
    }

    private BigDecimal calculateOriginalSum(List<SubmitOrderRequest.OrderItemRequest> items,
                                            Map<Long, BigDecimal> productPriceMap) {
        BigDecimal sum = BigDecimal.ZERO;
        for (SubmitOrderRequest.OrderItemRequest item : items) {
            BigDecimal originalPrice = productPriceMap.get(item.getProductId());
            sum = sum.add(originalPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return sum;
    }

    private void validateDiscountSum(BigDecimal expectedDiscountSum, BigDecimal requestDiscountSum) {
        if (expectedDiscountSum.compareTo(requestDiscountSum) != 0) {
            throw new IllegalArgumentException("총 할인 금액이 올바르지 않습니다.");
        }
    }

    private void validateFinalPayment(BigDecimal expectedFinalPayment, BigDecimal requestFinalPayment) {
        if (expectedFinalPayment.compareTo(requestFinalPayment) != 0) {
            throw new IllegalArgumentException("최종 결제 금액이 올바르지 않습니다.");
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.order.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.OrderItem;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.converter.OrderConverter;
import com.shop.shoppingapi.entity.converter.OrderItemConverter;
import com.shop.shoppingapi.redis.CartCacheRepository;
import com.shop.shoppingapi.repository.OrderRepository;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartCacheRepository cartCacheRepository;
    private final OrderValidationService orderValidationService;

    @Transactional
    public Long submitOrder(Long userId, SubmitOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        // 1. 요청 가격이랑 상품 실제 가격이 일치하는지 확인
        orderValidationService.validateOrder(request);

        // 2. 사용자 상태 및 포인트 검증
        orderValidationService.validateUserPoint(user, request.getUsedPoints());

        // 3. order 엔티티 및 orderItem 엔티티 생성
        Order order = createOrderAndOrderItems(user, request);
        Long savedOrderId = orderRepository.save(order).getId();

        // 11. 사용자 포인트 차감
        user.usePoints(request.getUsedPoints());
        userRepository.save(user);

        // 12. 장바구니 삭제 처리 (해당 상품들을 사용자 장바구니에서 삭제)
        cartCacheRepository.deleteCartItems(userId, request.getProductIds());

        return savedOrderId;
    }

    private Order createOrderAndOrderItems(User user, SubmitOrderRequest request) {
        // 1. order 엔티티 생성
        String orderNumber = generateOrderNumber();
        Order order = OrderConverter.toEntity(request, orderNumber, user);
        order.clearOrderItems();

        // 2. orderItems 엔티티 생성
        Map<Long, Product> productMap = getProductMap(request.getProductIds());
        List<OrderItem> orderItems = request.getItems().stream().map(itemReq -> OrderItemConverter.toEntity(order, itemReq, productMap.get(itemReq.getProductId()))).toList();
        order.addOrderItems(orderItems);
        return order;
    }

    private Map<Long, Product> getProductMap(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        return products.stream().collect(Collectors.toMap(Product::getId, a -> a));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional(readOnly = true)
    public Order findOrderById(Long orderId) {
        Optional<Order> findOrderOptional = orderRepository.findById(orderId);
        return findOrderOptional
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<Order> findOrdersWithOrdersItemsAndProductByUserId(long userId, Pageable pageable) {
        Page<Order> findPagedOrders = orderRepository.findAllByBuyerId(userId, pageable);
        if (!findPagedOrders.isEmpty()) {
            findPagedOrders.getContent().forEach(order -> order.getOrderItems().forEach(orderItem -> orderItem.getProduct().toString()));
        }
        return findPagedOrders;
    }
}

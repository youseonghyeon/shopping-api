package com.shop.shoppingapi.service;

import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.repository.OrderRepository;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<Order> findMyOrder(Pageable pageable) {
        if (SecurityUtils.getCurrentAuthentication() == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }
        Long userId = SecurityUtils.getUserId();
        return orderRepository.findByUserId(userId, pageable);
    }
}

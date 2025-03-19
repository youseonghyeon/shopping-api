package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.repository.querydsl.OrderRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Page<Order> findAllByBuyerId(long buyerId, Pageable pageable);
}

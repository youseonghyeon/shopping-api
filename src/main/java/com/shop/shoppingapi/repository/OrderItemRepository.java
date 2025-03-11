package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select oi from OrderItem oi join fetch oi.product where oi.id in :ids")
    List<OrderItem> findAllWithProductByIds(List<Long> ids);
}

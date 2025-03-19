package com.shop.shoppingapi.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shoppingapi.entity.QOrder;
import com.shop.shoppingapi.entity.QOrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean isPurchasedUser(Long userId, Long productId) {
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        return queryFactory.selectOne()
                .from(order)
                .join(order.orderItems, orderItem)
                .where(order.buyer.id.eq(userId)
                        .and(orderItem.product.id.eq(productId)))
                .fetchFirst() != null;

    }
}

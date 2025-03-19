package com.shop.shoppingapi.repository.querydsl;

public interface OrderRepositoryCustom {
    boolean isPurchasedUser(Long userId, Long productId);
}

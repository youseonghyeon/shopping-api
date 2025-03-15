package com.shop.shoppingapi.entity;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class WishlistConverter {

    public static Wishlist toEntity(User user, Product product) {
        return Wishlist.builder()
                .user(user)
                .product(product)
                .build();
    }
}

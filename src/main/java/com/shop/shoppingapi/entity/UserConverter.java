package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;

import java.util.List;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class UserConverter {

    public static User toEntity(CreateUserRequest createUserRequest, String encodedPassword) {
        return User.builder()
                .username(createUserRequest.getName())
                .password(encodedPassword)
                .email(createUserRequest.getEmail())
                .phone(createUserRequest.getPhone())
                .role(Role.ROLE_USER)
                .build();
    }

    public static User toEntity(String username, String encodedPassword, String email, String phone, int point, Role role, List<Wishlist> wishlists) {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .phone(phone)
                .point(point)
                .role(role)
                .wishlists(wishlists)
                .build();
    }
}

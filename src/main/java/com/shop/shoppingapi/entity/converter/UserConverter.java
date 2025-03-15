package com.shop.shoppingapi.entity.converter;

import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.entity.User;

import java.math.BigDecimal;

/**
 * Converter 는 꼭 비 영속 상태로 생성하도록 합니다.
 * 만약 팩토리가 필요 할 경우 기존 toEntity 를 수정하지 않고 추가합니다.
 */
public class UserConverter {

    public static User toEntity(CreateUserRequest createUserRequest, String encodedPassword) {
        return new User(createUserRequest.getName(), encodedPassword, createUserRequest.getEmail(), createUserRequest.getPhone(), Role.ROLE_USER);
    }
}

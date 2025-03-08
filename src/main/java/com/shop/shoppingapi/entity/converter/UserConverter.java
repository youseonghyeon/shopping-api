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

    public static User toEntity(CreateUserRequest request) {
        return new User(request.getName(), request.getPassword(), request.getEmail(), request.getPhone(), Role.USER);
    }

    public static Product toEntity(String name, String titleImage, String title, BigDecimal price, String description, String category, Integer stock) {
        return new Product(name, titleImage, title, price, description, category, stock);
    }
}

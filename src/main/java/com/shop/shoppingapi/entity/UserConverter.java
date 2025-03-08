package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;

public class UserConverter {

    public static User toEntity(CreateUserRequest createUserRequest, String encodedPassword) {
        return new User(createUserRequest.getName(), encodedPassword, createUserRequest.getEmail(), createUserRequest.getPhone(), Role.USER);
    }

    public static User toEntity(String username, String password, Role role) {
        return new User(username, password, "test", "test", role);
    }
}

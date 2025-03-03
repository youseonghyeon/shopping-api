package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.controller.dto.CreateUserRequest;

public class UserConverter {
    public static User toEntity(CreateUserRequest createUserRequest) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static User toEntity(String username, String password, Role role) {
        return new User(username, password, "test", "test", role);
    }
}

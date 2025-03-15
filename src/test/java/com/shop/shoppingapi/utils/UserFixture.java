package com.shop.shoppingapi.utils;

import com.shop.shoppingapi.controller.dto.user.CreateUserRequest;
import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.UserConverter;
import com.shop.shoppingapi.entity.Wishlist;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserFixture {
    private static String emailMock = "test@mail.com";
    private static String usernameMock = "홍길동";
    private static String passwordMock = "test1234!";
    private static String passwordConfirmMock = "test1234!";
    private static String phoneMock = "01012345678";

    public static CreateUserRequest toCreateUserRequest() {
        return toCreateUserRequest(null, null, null, null, null);
    }

    public static CreateUserRequest toCreateUserRequest(
            @Nullable String email,
            @Nullable String name,
            @Nullable String password,
            @Nullable String passwordConfirm,
            @Nullable String phone
    ) {
        return CreateUserRequest.builder()
                .email(Objects.isNull(email) ? emailMock : email)
                .name(Objects.isNull(name) ? usernameMock : name)
                .password(Objects.isNull(password) ? passwordMock : password)
                .passwordConfirm(Objects.isNull(passwordConfirm) ? passwordConfirmMock : passwordConfirm)
                .phone(Objects.isNull(phone) ? phoneMock : phone)
                .build();
    }

    private static int pointMock = 10000;
    private static Role roleMock = Role.ROLE_USER;
    private static List<Wishlist> wishlistsMock = new ArrayList<>();

    public static User toUser() {
        return toUser(null, null, null, null, null, null, null);
    }
    public static User toUser(
            @Nullable String username,
            @Nullable String password,
            @Nullable String email,
            @Nullable String phone,
            @Nullable Integer point,
            @Nullable Role role,
            @Nullable List<Wishlist> wishlists
    ) {
        username = Objects.isNull(username) ? emailMock : username;
        password = Objects.isNull(password) ? passwordMock : password;
        email = Objects.isNull(email) ? emailMock : email;
        phone = Objects.isNull(phone) ? phoneMock : phone;
        point = Objects.isNull(point) ? pointMock : point;
        role = Objects.isNull(role) ? roleMock : role;
        wishlists = Objects.isNull(wishlists) || wishlists.isEmpty() ? wishlistsMock : wishlists;
        return UserConverter.toEntity(username, password, email, phone, point, role, wishlists);
    }
}

package com.shop.shoppingapi.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SimpleUserResponse {

    @JsonIgnore
    private Long id;
    private String username;
    private String email;
    private String phone;
    private int points;
    private Role role;

    public static SimpleUserResponse from(User user) {
        return new SimpleUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getPoint(),
                user.getRole()
        );
    }
}

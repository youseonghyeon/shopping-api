package com.shop.shoppingapi.security.model;

import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private int point;
    private Role role;

    public static SimpleUser fromEntity(User user) {
        return new SimpleUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                user.getPoint(),
                user.getRole()
        );
    }
}

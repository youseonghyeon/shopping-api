package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.security.model.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserTestUtils {

    private UserTestUtils() {
    }

    public static User createUser(Long userId, String username, String password, String email, String phone) {
        return new User(userId, username, password, email, phone, 0, Role.USER);
    }

    public static User login(Long userId, String username, String password, String email, String phone) {
        User user = createUser(userId, username, password, email, phone);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return user;
    }
}

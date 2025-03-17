package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.security.model.CustomUserDetails;
import com.shop.shoppingapi.security.model.SimpleUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TestUtils {

    private TestUtils() {
    }

    public static User createUser(Long userId, String username, String password, String email, String phone) {
        return new User(userId, username, password, email, phone, 0, Role.ROLE_USER, new ArrayList<>());
    }

    public static User login(Long userId, String username, String password, String email, String phone) {
        User user = createUser(userId, username, password, email, phone);
        CustomUserDetails userDetails = new CustomUserDetails(SimpleUser.fromEntity(user));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return user;
    }

    public static Product createProduct(Long productId, String name, String titleImage, String title, BigDecimal price) {
        return new Product(productId, name, titleImage, title, price, "description", "category", 100, 0.0, new ArrayList<>(), new ArrayList<>());
    }
}

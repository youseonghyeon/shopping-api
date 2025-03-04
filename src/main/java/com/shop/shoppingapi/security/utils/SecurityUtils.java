package com.shop.shoppingapi.security.utils;

import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.security.model.CustomUserDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    public static Long getUserId() {
        return Optional.ofNullable(getCurrentUser())
                .map(CustomUserDetails::user)
                .map(User::getId)
                .orElse(null);
    }

    private static CustomUserDetails getCurrentUser() {
        return (CustomUserDetails) Optional.ofNullable(getCurrentAuthentication())
                .map(Authentication::getPrincipal).orElse(null);
    }

    public static Authentication getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication).orElse(null);
    }


}

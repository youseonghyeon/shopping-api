package com.shop.shoppingapi.security.utils;

import com.shop.shoppingapi.security.model.CustomUserDetails;
import com.shop.shoppingapi.security.model.SimpleUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    private static final String AUTH_CREDENTIALS_NOT_FOUND = "No authentication credentials found.";

    public static Long getUserId() {
        return Optional.ofNullable(getCurrentUser())
                .map(CustomUserDetails::simpleUser)
                .map(SimpleUser::getId)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(AUTH_CREDENTIALS_NOT_FOUND));
    }

    public static CustomUserDetails getCurrentUser() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(AUTH_CREDENTIALS_NOT_FOUND));
    }

    private static Authentication getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(AUTH_CREDENTIALS_NOT_FOUND));
    }
}

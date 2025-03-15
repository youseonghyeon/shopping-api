package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.security.model.CustomUserDetails;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        return Optional.of(currentUser.user());
    }

}

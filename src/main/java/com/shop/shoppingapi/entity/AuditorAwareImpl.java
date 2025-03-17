package com.shop.shoppingapi.entity;

import com.shop.shoppingapi.repository.UserRepository;
import com.shop.shoppingapi.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Long userId = SecurityUtils.getUserId();
        return userRepository.findById(userId);
    }

}

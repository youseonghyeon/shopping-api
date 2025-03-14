package com.shop.shoppingapi.security.model;

import com.shop.shoppingapi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record CustomUserDetails(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO DB 필드 추가 후 사용 예정
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO DB 필드 추가 후 사용 예정
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO DB 필드 추가 후 사용 예정
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO DB 필드 추가 후 사용 예정
        return true;
    }

}

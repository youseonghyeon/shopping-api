package com.shop.shoppingapi.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record CustomUserDetails(SimpleUser simpleUser) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(simpleUser.getRole().name()));
    }

    @Override
    public String getPassword() {
        return simpleUser.getPassword();
    }

    @Override
    public String getUsername() {
        return simpleUser.getEmail();
    }

    public Long getUserId() {
        return simpleUser.getId();
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

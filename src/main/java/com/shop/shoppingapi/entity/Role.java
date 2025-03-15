package com.shop.shoppingapi.entity;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER("일반 사용자"),
    ROLE_SELLER("판매자"),
    ROLE_MANAGER("관리자"),
    ROLE_ADMIN("운영자");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}

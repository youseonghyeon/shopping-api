package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    private String phone;

    private BigDecimal point = BigDecimal.ZERO;

    // 예시로 하나의 역할(role)만 저장 (복수 권한은 Set<String> 등으로 구현)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String username, String password, String email, String phone, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public void addPoints(BigDecimal bigDecimal) {
        this.point = this.point.add(bigDecimal);
    }
}

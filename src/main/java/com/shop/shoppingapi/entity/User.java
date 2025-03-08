package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private int point = 0;

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

    public void addPoints(int point) {
        if (point < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
        this.point += point;
    }

    public void usePoints(int point) {
        if (point < 0) {
            throw new IllegalArgumentException("사용할 포인트는 0 이상이어야 합니다.");
        } else if (this.point < point) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.point -= point;
    }
}

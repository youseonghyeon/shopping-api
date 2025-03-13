package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    @OneToMany(mappedBy = "user")
    private List<Wishlist> wishlists = new ArrayList<>();

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

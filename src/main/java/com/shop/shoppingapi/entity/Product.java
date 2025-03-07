package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 제품 ID

    private String name; // 제품 이름

    @Column(name = "title_image")
    private String titleImage; // 타이틀 이미지 URL

    private String title; // 제품 타이틀 또는 간단 설명

    private BigDecimal price; // 제품 가격

    private String description; // 상세 설명

    private String category; // 제품 카테고리

    private Integer stock; // 재고 수량

    private Double discountRate = 0.0;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> review = new ArrayList<>();

    public Product(String name, String titleImage, String title, BigDecimal price, String description, String category, Integer stock) {
        this.name = name;
        this.titleImage = titleImage;
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
    }

    public Product(String name, String titleImage, String title, BigDecimal price, String description, String category, Integer stock, Double discountRate) {
        this.name = name;
        this.titleImage = titleImage;
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
        this.discountRate = discountRate;
    }
}

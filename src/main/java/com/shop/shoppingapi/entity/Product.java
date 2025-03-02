package com.shop.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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


}

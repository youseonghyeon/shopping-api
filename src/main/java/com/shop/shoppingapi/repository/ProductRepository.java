package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

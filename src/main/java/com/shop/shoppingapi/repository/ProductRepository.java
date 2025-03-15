package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.repository.querydsl.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("SELECT p FROM Product p left join fetch p.reviews WHERE p.id = :id")
    Optional<Product> findWithReviewsById(Long id);
}

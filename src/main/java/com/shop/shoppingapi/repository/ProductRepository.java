package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.repository.querydsl.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Page<Product> findAllByNameContaining(String name, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r LEFT JOIN Wishlist w ON p.id = w.product.id GROUP BY p ORDER BY (COUNT(r) + COUNT(w)) DESC")
    Page<Product> findRecommendedProducts(Pageable pageable);
}

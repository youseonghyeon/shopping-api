package com.shop.shoppingapi.repository;

import com.shop.shoppingapi.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
    List<Wishlist> findByUser_Id(Long userId);
}

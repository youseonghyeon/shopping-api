package com.shop.shoppingapi.repository.querydsl;

import com.shop.shoppingapi.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> findProductsInQueryDsl(Pageable pageable, String containsName);


}

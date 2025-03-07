package com.shop.shoppingapi.service.dto;

import com.shop.shoppingapi.redis.dto.CartItem;
import com.shop.shoppingapi.redis.dto.SimpleProduct;
import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
    private Long productId;
    private int quantity;
    private BigDecimal originalPrice;
    private Double discountRate;
    private BigDecimal discountedPrice;

    private String productName;
    private String productTitleImage;

    public static Cart join(CartItem cartItem, SimpleProduct simpleProduct) {
        return new Cart(
                cartItem.getProductId(),
                cartItem.getQuantity(),
                simpleProduct.getOriginalPrice(),
                simpleProduct.getDiscountRate(),
                simpleProduct.getDiscountedPrice(),
                simpleProduct.getProductName(),
                simpleProduct.getProductTitleImage()
        );
    }

}

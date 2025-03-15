package com.shop.shoppingapi.utils;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.Wishlist;
import com.shop.shoppingapi.entity.WishlistConverter;
import org.springframework.lang.Nullable;

import java.util.Objects;

public class WishlistFixture {
    public static Wishlist toWishlist(
            @Nullable User user,
            @Nullable Product product
    ) {
        user = Objects.requireNonNullElseGet(user, UserFixture::toUser);
        product = Objects.requireNonNullElseGet(product, ProductFixture::toProduct);
        return WishlistConverter.toEntity(user, product);
    }
}

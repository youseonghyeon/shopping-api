package com.shop.shoppingapi.controller.dto;

import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.Wishlist;
import lombok.Data;

@Data
public class CreateWishlistRequest {

    private Long productId;
}

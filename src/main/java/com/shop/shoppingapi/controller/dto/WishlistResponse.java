package com.shop.shoppingapi.controller.dto;

import com.shop.shoppingapi.controller.dto.product.ProductResponse;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WishlistResponse {

    private List<ProductResponse> products;

}

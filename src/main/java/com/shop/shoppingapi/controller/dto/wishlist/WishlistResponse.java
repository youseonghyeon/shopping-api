package com.shop.shoppingapi.controller.dto.wishlist;

import com.shop.shoppingapi.controller.dto.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WishlistResponse {

    private List<ProductResponse> products;

}

package com.shop.shoppingapi.service;

import com.shop.shoppingapi.redis.CartCacheRepository;
import com.shop.shoppingapi.redis.dto.CartItem;
import com.shop.shoppingapi.redis.dto.ProductPrice;
import com.shop.shoppingapi.service.dto.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartCacheRepository cartCacheRepository;
    private final ProductService productService;

    public void addCartItem(Long userId, Long productId, int quantity) {
        cartCacheRepository.addOrUpdateCartItem(userId, productId, quantity);
    }

    public void updateCartItem(Long userId, Long productId, Integer quantity) {
        cartCacheRepository.addOrUpdateCartItem(userId, productId, quantity);
    }

    public List<Cart> findCarts(Long userId) {
        List<CartItem> cartItems = cartCacheRepository.getCartItems(userId);
        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).toList();
        Map<Long, ProductPrice> productPricesByIds = productService.findProductPricesByIds(productIds);
        return cartItems.stream()
                .map(cartItem -> {
                    ProductPrice productPrice = productPricesByIds.get(cartItem.getProductId());
                    return Cart.join(cartItem, productPrice);
                })
                .toList();
    }

    public void deleteCartItem(Long userId, Long productId) {
        cartCacheRepository.removeCartItem(userId, productId);
    }



}

package com.shop.shoppingapi.service;

import com.shop.shoppingapi.redis.CartCacheRepository;
import com.shop.shoppingapi.redis.dto.CartItem;
import com.shop.shoppingapi.redis.dto.SimpleProduct;
import com.shop.shoppingapi.controller.dto.cart.CartResponse;
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
        int existingQuantity = cartCacheRepository.getCartItemQuantity(userId, productId);
        cartCacheRepository.addOrUpdateCartItem(userId, productId, existingQuantity + quantity);
    }

    public void updateCartItem(Long userId, Long productId, Integer quantity) {
        if (quantity > 0) {
            cartCacheRepository.addOrUpdateCartItem(userId, productId, quantity);
        } else {
            this.deleteCartItem(userId, List.of(productId));
        }
    }

    public void deleteCartItem(Long userId, List<Long> productIds) {
        productIds.forEach(id -> cartCacheRepository.deleteCartItem(userId, id));
    }

    public List<CartResponse> findCarts(Long userId) {
        List<CartItem> cartItems = cartCacheRepository.getCartItems(userId);
        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).toList();
        Map<Long, SimpleProduct> simpleProductByIds = productService.findSimpleProductByIds(productIds);
        return cartItems.stream()
                .map(cartItem -> {
                    SimpleProduct simpleProduct = simpleProductByIds.get(cartItem.getProductId());
                    return CartResponse.join(cartItem, simpleProduct);
                })
                .toList();
    }

}

package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.cart.CartResponse;
import com.shop.shoppingapi.exception.ApiResponseException;
import com.shop.shoppingapi.redis.CartCacheRepository;
import com.shop.shoppingapi.redis.dto.CartItem;
import com.shop.shoppingapi.redis.dto.SimpleProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartCacheRepository cartCacheRepository;
    private final ProductService productService;

    public void addCartItem(Long userId, Long productId, int quantity) {
        if (!productService.existsProductById(productId)) {
            log.error("Product not found - productId: {}", productId);
            throw new ApiResponseException("Product not Found", HttpStatus.BAD_REQUEST);
        }
        int existingQuantity = cartCacheRepository.getCartItemQuantity(userId, productId);
        cartCacheRepository.addOrUpdateCartItem(userId, productId, existingQuantity + quantity);
    }

    public void updateCartItem(Long userId, Long productId, Integer quantity) {
        if (quantity > 0) {
            cartCacheRepository.addOrUpdateCartItem(userId, productId, quantity);
        } else {
            log.warn("Delete cart item (request quantity: {})- userId: {}, productId: {}", quantity, userId, productId);
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

    public int findCartsSize(Long userId) {
        return cartCacheRepository.getCartSize(userId);
    }
}

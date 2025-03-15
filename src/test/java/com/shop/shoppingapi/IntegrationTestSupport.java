package com.shop.shoppingapi;

import com.shop.shoppingapi.entity.Order;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.Wishlist;
import com.shop.shoppingapi.repository.OrderRepository;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.repository.UserRepository;
import com.shop.shoppingapi.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    protected User save(User user) {
        return userRepository.save(user);
    }

    @Autowired
    private ProductRepository productRepository;

    protected Product save(Product product) {
        return productRepository.save(product);
    }

    @Autowired
    private WishlistRepository wishlistRepository;

    protected Wishlist save(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }


    @Autowired
    OrderRepository orderRepository;

    protected Order save(Order order) {
        return orderRepository.save(order);
    }
}

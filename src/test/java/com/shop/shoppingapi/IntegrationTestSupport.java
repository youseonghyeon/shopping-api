package com.shop.shoppingapi;

import com.shop.shoppingapi.entity.*;
import com.shop.shoppingapi.repository.*;
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
    private OrderRepository orderRepository;

    protected Order save(Order order) {
        return orderRepository.save(order);
    }

    @Autowired
    private ReviewRepository reviewRepository;

    protected Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Autowired
    private OrderItemRepository orderItemRepository;

    protected OrderItem save(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
}

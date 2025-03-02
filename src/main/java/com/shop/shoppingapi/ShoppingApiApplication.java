package com.shop.shoppingapi;

import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ShoppingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingApiApplication.class, args);
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("init");
        userRepository.save(User.of("user", passwordEncoder.encode("user"), "USER"));
    }

}

package com.shop.shoppingapi.dev;

import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.entity.UserConverter;
import com.shop.shoppingapi.entity.converter.ProductConverter;
import com.shop.shoppingapi.repository.ProductRepository;
import com.shop.shoppingapi.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

@Slf4j
@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class DevDataSeeder {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void seedData() {
        CompletableFuture.runAsync(() -> {
            log.info("Starting data seeder initialization");
            insertProducts(100);
            insertUsers(100);
            // insert etc ...
            log.info("Data seeder initialization finished");
        });
    }

    private String[] productImages = {"https://thumbnail8.coupangcdn.com/thumbnails/remote/320x320ex/image/1025_amir_coupang_oct_80k/eb38/aa9d608f2d516bc2ac5dc44a5d07cadaebe11e58409cfd02eea64e1e3d32.jpg",
            "https://thumbnail9.coupangcdn.com/thumbnails/remote/320x320ex/image/retail/images/2024/11/08/13/4/d01c4dd5-5da1-4fb6-a042-8c2002e8fa6e.jpg",
            "https://thumbnail10.coupangcdn.com/thumbnails/remote/320x320ex/image/retail/images/2024/11/04/16/0/ee63c2ec-17c7-447d-8a9a-9e4dea58ac76.jpeg",
            "https://thumbnail7.coupangcdn.com/thumbnails/remote/320x320ex/image/retail/images/2024/11/06/13/6/1affc1cc-67bd-42a5-8ea9-f2be0db7c09d.jpeg",
            "https://thumbnail8.coupangcdn.com/thumbnails/remote/230x230ex/image/retail/images/154081775986418-030eb1bf-4bd3-499f-9201-3608f4418928.jpg",
            "https://image6.coupangcdn.com/image/vendor_inventory/e154/b8bd63cdafc623daccde4ae1c0bb686570fec8e2c530bc597857ba5915b3.jpg"};

    private String[] productNames = {"Apple 2024 맥북 프로 14 M4",
            "Apple 2024 맥북 프로 14 M4",
            "Apple 2024 맥북 에어 13 M3",
            "Apple 맥북 에어 13 M2",
            "다루미 맥북에어 그램 스탠드 가방, 블랙, 1개",
            "UGREEN 유그린 1000Mbps USB C 이더넷 어댑터 10Gbps USB3.2 Gen2 5 in 1 USB C 허브 노트북 맥북 윈도우 XPS 아이패드 프로 이더넷 연결용"};

    private Long[] productPrices = {2000_000L, 2_500_000L, 1_500_000L, 1_200_000L, 50_000L, 30_000L};

    private void insertUsers(int mockUserSize) {
        String usernamePrefix = "user";
        String passwordPrefix = "user";
        Role role = Role.USER;
        List<User> list = LongStream.range(0, mockUserSize)
                .mapToObj(i -> UserConverter.toEntity(usernamePrefix + i, passwordEncoder.encode(passwordPrefix + i), role))
                .toList();
        userRepository.saveAll(list);
    }

    private void insertProducts(int mockProductSize) {
        List<Product> list = LongStream.range(0, mockProductSize)
                .mapToObj(i -> ProductConverter.toEntity(getRandomName(), getRandomImage(), getRandomName(), getRandomPrice(), "description", "category", 100))
                .toList();
        productRepository.saveAll(list);
    }

    private String getRandomImage() {
        return productImages[(int) (Math.random() * productImages.length)];
    }

    private String getRandomName() {
        return productNames[(int) (Math.random() * productNames.length)];
    }

    private BigDecimal getRandomPrice() {
        return BigDecimal.valueOf(productPrices[(int) (Math.random() * productPrices.length)]);
    }

}

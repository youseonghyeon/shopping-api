package com.shop.shoppingapi.config;

import com.shop.shoppingapi.entity.AuditorAwareImpl;
import com.shop.shoppingapi.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    @Bean
    public AuditorAware<User> auditorAware() {
        return new AuditorAwareImpl();
    }
}

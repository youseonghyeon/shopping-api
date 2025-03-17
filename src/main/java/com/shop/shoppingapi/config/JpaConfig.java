package com.shop.shoppingapi.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shoppingapi.entity.AuditorAwareImpl;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public AuditorAware<User> auditorAware(UserRepository userRepository) {
        return new AuditorAwareImpl(userRepository);
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}

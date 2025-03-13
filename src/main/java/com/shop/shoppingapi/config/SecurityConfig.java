package com.shop.shoppingapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.shop.shoppingapi.security.handler.CustomAuthenticationFailureHandler;
import com.shop.shoppingapi.security.handler.CustomAuthenticationSuccessHandler;
import com.shop.shoppingapi.security.handler.CustomLogoutSuccessHandler;
import com.shop.shoppingapi.security.service.CustomUserDetailsService;
import com.shop.shoppingapi.security.utils.RsaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${security.rsa.private-key-path}")
    private String rsaPrivateKeyPath;

    @NotNull
    private static Customizer<LogoutConfigurer<HttpSecurity>> logoutStep() {
        log.trace("Configuring logoutStep for SecurityFilterChain");
        return logout -> logout.logoutUrl("/api/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessHandler(new CustomLogoutSuccessHandler());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   RememberMeServices rememberMeServices,
                                                   RsaUtils rsaUtils) throws Exception {
        log.trace("Initializing SecurityFilterChain bean");
        JsonUsernamePasswordAuthenticationFilter jsonLoginFilter = new JsonUsernamePasswordAuthenticationFilter(authenticationManager, rememberMeServices, rsaUtils);
        jsonLoginFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler());
        jsonLoginFilter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());

        SecurityFilterChain filterChain = http
                .cors(httpCors -> httpCors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/cart/**").authenticated()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logoutStep())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .addFilterBefore(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
        log.trace("SecurityFilterChain bean initialized successfully");
        return filterChain;
    }

    @Bean("rsaUtils")
    public RsaUtils rsaUtils() {
        log.trace("Initializing RsaUtils bean with private key path: {}", rsaPrivateKeyPath);
        RsaUtils rsaUtils = new RsaUtils(rsaPrivateKeyPath);
        log.trace("RsaUtils bean initialized successfully");
        return rsaUtils;
    }

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        log.trace("Initializing BCryptPasswordEncoder bean");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        log.trace("BCryptPasswordEncoder bean initialized");
        return encoder;
    }

    @Bean("authenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        log.trace("Initializing AuthenticationManager bean");
        AuthenticationManager authenticationManager = authConfig.getAuthenticationManager();
        log.trace("AuthenticationManager bean initialized");
        return authenticationManager;
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        log.trace("Initializing CustomAuthenticationFailureHandler bean");
        AuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler();
        log.trace("CustomAuthenticationFailureHandler bean initialized");
        return handler;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        log.trace("Initializing CorsConfigurationSource bean");
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOriginList = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toList();
        configuration.setAllowedOrigins(allowedOriginList);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        log.trace("CorsConfigurationSource bean initialized with allowed origins: {}", allowedOriginList);
        return source;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        log.trace("Initializing RoleHierarchy bean");
        String hierarchy = String.join(" \n ",
                Role.ROLE_ADMIN.name() + " > " + Role.ROLE_MANAGER.name(),
                Role.ROLE_MANAGER + " > " + Role.ROLE_SELLER.name(),
                Role.ROLE_SELLER + " > " + Role.ROLE_USER.name()
        );
        RoleHierarchy roleHierarchy = RoleHierarchyImpl.fromHierarchy(hierarchy);
        log.trace("RoleHierarchy bean initialized with hierarchy:\n{}", hierarchy);
        return roleHierarchy;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        log.trace("Initializing PersistentTokenRepository bean");
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        log.trace("PersistentTokenRepository bean initialized");
        return tokenRepository;
    }

    @Bean
    public RememberMeServices rememberMeServices(CustomUserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        log.trace("Initializing RememberMeServices bean");
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices("a6e5f8d2c1b4a3d7e9f0b1c2a3d4e5f6", userDetailsService, tokenRepository);
        rememberMeServices.setTokenValiditySeconds(14 * 24 * 60 * 60);
        rememberMeServices.setParameter("rememberMe");
        log.trace("RememberMeServices bean initialized with key: {}", "a6e5f8d2c1b4a3d7e9f0b1c2a3d4e5f6");
        return rememberMeServices;
    }
}

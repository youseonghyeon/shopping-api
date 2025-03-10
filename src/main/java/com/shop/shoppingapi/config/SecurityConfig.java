package com.shop.shoppingapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.entity.Role;
import com.shop.shoppingapi.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.shop.shoppingapi.security.handler.CustomAuthenticationFailureHandler;
import com.shop.shoppingapi.security.handler.CustomAuthenticationSuccessHandler;
import com.shop.shoppingapi.security.handler.CustomLogoutSuccessHandler;
import com.shop.shoppingapi.security.service.CustomUserDetailsService;
import com.shop.shoppingapi.security.service.JwtTokenProvider;
import com.shop.shoppingapi.security.utils.RsaUtils;
import lombok.RequiredArgsConstructor;
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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${security.rsa.private-key-path}")
    private String rsaPrivateKeyPath;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            RememberMeServices rememberMeServices,
            RsaUtils rsaUtils) throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonLoginFilter = new JsonUsernamePasswordAuthenticationFilter(authenticationManager, rememberMeServices, rsaUtils);
        jsonLoginFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler(objectMapper, jwtTokenProvider));
        jsonLoginFilter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());

        return http
                .cors(httpCors -> httpCors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
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
    }

    @NotNull
    private static Customizer<LogoutConfigurer<HttpSecurity>> logoutStep() {
        return logout -> logout.logoutUrl("/api/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessHandler(new CustomLogoutSuccessHandler());
    }

    @Bean
    public RsaUtils rsaUtils() {
        return new RsaUtils(rsaPrivateKeyPath);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOriginList = Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList();
        configuration.setAllowedOrigins(allowedOriginList);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = String.join(" \n ",
                Role.ROLE_ADMIN.name() + " > " + Role.ROLE_MANAGER.name(),
                Role.ROLE_MANAGER + " > " + Role.ROLE_SELLER.name(),
                Role.ROLE_SELLER + " > " + Role.ROLE_USER.name()
        );
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public RememberMeServices rememberMeServices(CustomUserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices("a6e5f8d2c1b4a3d7e9f0b1c2a3d4e5f6", userDetailsService, tokenRepository);
        rememberMeServices.setTokenValiditySeconds(14 * 24 * 60 * 60);
        rememberMeServices.setParameter("rememberMe");
        return rememberMeServices;
    }

}

package com.shop.shoppingapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.shoppingapi.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.shop.shoppingapi.security.handler.CustomAuthenticationFailureHandler;
import com.shop.shoppingapi.security.handler.CustomAuthenticationSuccessHandler;
import com.shop.shoppingapi.security.handler.CustomLogoutSuccessHandler;
import com.shop.shoppingapi.security.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonLoginFilter = new JsonUsernamePasswordAuthenticationFilter(authenticationManager);
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
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new CustomLogoutSuccessHandler());
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

}

package com.shop.shoppingapi.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.shoppingapi.controller.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static String findFailureReason(AuthenticationException exception) {
        String failureReason;
        if (exception instanceof BadCredentialsException) {
            failureReason = "아이디 또는 패스워드가 일치하지 않습니다.";
        } else if (exception instanceof LockedException) {
            failureReason = "사용자 계정이 잠겨 있습니다.";
        } else if (exception instanceof DisabledException) {
            failureReason = "사용자 계정이 비활성화되어 있습니다.";
        } else {
            failureReason = exception.getMessage();
        }
        return failureReason;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.warn("Authentication failed: {}", exception.getMessage());
        String failureReason = findFailureReason(exception);

        ApiResponse<Void> body = ApiResponse.error(failureReason, HttpStatus.UNAUTHORIZED).getBody();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

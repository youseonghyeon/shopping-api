package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import com.shop.shoppingapi.exception.ApiResponseException;
import com.shop.shoppingapi.service.DuplicateResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateException(DuplicateResourceException ex) {
        log.error("", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("field", ex.getField());
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<? extends ApiResponse<?>> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        log.warn("Call without authentication credentials");
        return ApiResponse.error("인증 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiResponseException.class)
    public ResponseEntity<? extends ApiResponse<?>> handleApiResponseException(ApiResponseException ex) {
        log.error("", ex);
        return ApiResponse.error(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<? extends ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("", ex);
        return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<? extends ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ApiResponse.error("요청 데이터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST, Map.of("errors", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<? extends ApiResponse<?>> handleException(Exception ex) {
        log.error("", ex);
        return ApiResponse.error("handleException hit", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

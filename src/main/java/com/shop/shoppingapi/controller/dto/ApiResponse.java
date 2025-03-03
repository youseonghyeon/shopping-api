package com.shop.shoppingapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 공통 API 응답 클래스
 */
@Data
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

    private final String status;
    private final String message;
    private final T data;
    private final Object errorDetails;
    private final Instant timestamp;

    private ApiResponse(String status, String message, T data, Object errorDetails) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.errorDetails = errorDetails;
        this.timestamp = Instant.now(); // UTC 시간 기준
    }

    // 성공 응답
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", message, data, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "요청이 성공적으로 처리되었습니다.", data, null));
    }

    // 실패 응답
    public static ResponseEntity<ApiResponse<Void>> error(String message, HttpStatus status, Object errorDetails) {
        return ResponseEntity.status(status).body(new ApiResponse<>("FAIL", message, null, errorDetails));
    }

    public static ResponseEntity<ApiResponse<Void>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiResponse<>("FAIL", message, null, null));
    }

    public static ResponseEntity<ApiResponse<Void>> bindingResultError(String message, BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ApiResponse.error(message, HttpStatus.BAD_REQUEST, Map.of("errors", errors));
    }

}

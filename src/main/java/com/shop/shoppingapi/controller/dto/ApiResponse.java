package com.shop.shoppingapi.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.shop.shoppingapi.controller.dto.ApiResponse.Status.ERROR;
import static com.shop.shoppingapi.controller.dto.ApiResponse.Status.SUCCESS;

/**
 * 공통 API 응답 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Status status;

    private int code;

    private String message;

    private T data;

    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(SUCCESS)
                .code(200)
                .message("Operation successful")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(SUCCESS)
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .status(ERROR)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public enum Status {
        SUCCESS, ERROR
    }
}

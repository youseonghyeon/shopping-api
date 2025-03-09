package com.shop.shoppingapi.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponseException extends RuntimeException {

    private final HttpStatus httpStatus;

    public ApiResponseException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ApiResponseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApiResponseException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public ApiResponseException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }
}

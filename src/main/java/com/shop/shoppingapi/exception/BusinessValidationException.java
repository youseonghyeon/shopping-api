package com.shop.shoppingapi.exception;

public class BusinessValidationException extends IllegalArgumentException {
    public BusinessValidationException() {
        super();
    }

    public BusinessValidationException(String s) {
        super(s);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessValidationException(Throwable cause) {
        super(cause);
    }
}

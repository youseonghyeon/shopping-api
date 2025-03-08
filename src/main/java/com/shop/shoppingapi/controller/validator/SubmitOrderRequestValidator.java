package com.shop.shoppingapi.controller.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SubmitOrderRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}

package com.learn.micro.songservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearRangeValidator implements ConstraintValidator<YearRange, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (!value.matches("^(19|20)\\d{2}$")) {
            return false;
        }
        try {
            int year = Integer.parseInt(value);
            return year >= 1900 && year <= 2099;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

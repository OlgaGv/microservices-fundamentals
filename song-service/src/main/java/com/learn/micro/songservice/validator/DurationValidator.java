package com.learn.micro.songservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationValidator implements ConstraintValidator<DurationFormat, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        if (!value.matches("^\\d{2}:\\d{2}$")) {
            return false;
        }
        try {
            String[] parts = value.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return minutes >= 0 && seconds >= 0 && seconds < 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

package com.learn.micro.songservice.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = YearRangeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YearRange {

    String message() default "Year must be in the range 1900-2099.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

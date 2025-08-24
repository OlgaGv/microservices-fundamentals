package com.learn.micro.songservice.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = DurationValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationFormat {

    String message() default "Duration must be in the format MM:SS, with valid minute and second values.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

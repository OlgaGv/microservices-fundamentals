package com.learn.micro.resourceservice.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.exception.SimpleErrorResponse;
import com.learn.micro.resourceservice.exception.ValidationErrorResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice(annotations = RestController.class)
public class MainExceptionHandler {

    @ExceptionHandler(exception = {IllegalArgumentException.class,
        ConstraintViolationException.class})
    public ResponseEntity<SimpleErrorResponse> handleIllegalArgumentException(Exception e) {
        SimpleErrorResponse errorResponse = new SimpleErrorResponse(e.getMessage(),
            HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<SimpleErrorResponse> handleResourceNotFoundException(
        ResourceNotFoundException e) {
        SimpleErrorResponse errorResponse = new SimpleErrorResponse(e.getMessage(),
            HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
        MethodArgumentNotValidException e) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ValidationErrorResponse errorResponse = new ValidationErrorResponse("Validation error.",
            validationErrors, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(GeneralFailureException.class)
    public ResponseEntity<SimpleErrorResponse> handleGenericException(GeneralFailureException e) {
        SimpleErrorResponse errorResponse = new SimpleErrorResponse(e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}

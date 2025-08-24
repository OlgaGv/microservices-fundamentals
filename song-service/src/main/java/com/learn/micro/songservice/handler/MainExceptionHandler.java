package com.learn.micro.songservice.handler;

import com.learn.micro.songservice.exception.GeneralFailureException;
import com.learn.micro.songservice.model.MainErrorResponse;
import com.learn.micro.songservice.model.ValidationMainErrorResponse;
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

import jakarta.persistence.EntityExistsException;

@RestControllerAdvice(annotations = RestController.class)
public class MainExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MainErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        MainErrorResponse errorResponse = new MainErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MainErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        MainErrorResponse errorResponse = new MainErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<MainErrorResponse> handleEntityExistsException(EntityExistsException e) {
        MainErrorResponse errorResponse = new MainErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationMainErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ValidationMainErrorResponse errorResponse = new ValidationMainErrorResponse(
                "Validation error",
                validationErrors,
                HttpStatus.BAD_REQUEST.value()
                );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(GeneralFailureException.class)
    public ResponseEntity<MainErrorResponse> handleGenericException(GeneralFailureException e) {
        MainErrorResponse errorResponse = new MainErrorResponse(
                "An error occurred on the server.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
                );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

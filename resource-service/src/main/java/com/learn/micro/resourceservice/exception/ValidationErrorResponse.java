package com.learn.micro.resourceservice.exception;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationErrorResponse extends SimpleErrorResponse {

    private Map<String, String> details;

    public ValidationErrorResponse(String errorMessage, Map<String, String> details, Integer errorCode) {
        super(errorMessage, errorCode);
        this.details = details;
    }
}

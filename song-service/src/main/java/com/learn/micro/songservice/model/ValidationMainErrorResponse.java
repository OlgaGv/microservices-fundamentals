package com.learn.micro.songservice.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationMainErrorResponse extends MainErrorResponse {

    private Map<String, String> details;

    public ValidationMainErrorResponse(String errorMessage, Map<String, String> details, Integer errorCode) {
        super( errorMessage, errorCode);
        this.details = details;
    }
}

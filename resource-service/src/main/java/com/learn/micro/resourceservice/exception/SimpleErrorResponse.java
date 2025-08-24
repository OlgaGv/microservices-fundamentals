package com.learn.micro.resourceservice.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleErrorResponse {

    private String errorMessage;
    private Integer errorCode;

    public SimpleErrorResponse(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}

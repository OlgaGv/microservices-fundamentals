package com.learn.micro.songservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainErrorResponse {

    private String errorMessage;
    private Integer errorCode;

    public MainErrorResponse(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}

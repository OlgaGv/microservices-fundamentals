package com.learn.micro.resourceservice.exception;

public class GeneralFailureException extends RuntimeException {

    public GeneralFailureException(String message) {
        super(message);
    }

    public GeneralFailureException(Throwable cause) {
        super(cause);
    }

    public GeneralFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}

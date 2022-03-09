package com.chenkaiwei.krest.exceptions;

public class KrestAuthenticationException extends KrestException {
    public KrestAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KrestAuthenticationException(String message) {
        super(message);
    }
}

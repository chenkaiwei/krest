package com.chenkaiwei.krest.exceptions;

public class KrestTokenException extends KrestAuthenticationException {

    public KrestTokenException(String message) {
        super(message);
    }

    public KrestTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

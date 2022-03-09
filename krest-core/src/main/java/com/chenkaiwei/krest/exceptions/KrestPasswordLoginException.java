package com.chenkaiwei.krest.exceptions;

public class KrestPasswordLoginException extends KrestAuthenticationException {

    public KrestPasswordLoginException(String message) {
        super(message);
    }

    public KrestPasswordLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}

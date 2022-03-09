package com.chenkaiwei.krest.exceptions;

public class KrestException extends RuntimeException{

    public KrestException() {
    }

    public KrestException(String message) {
        super(message);
    }

    public KrestException(String message, Throwable cause) {
        super(message, cause);
    }

    public KrestException(Throwable cause) {
        super(cause);
    }

    public KrestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.chenkaiwei.krest.exceptions;

import org.apache.shiro.authz.UnauthorizedException;

public class KrestUnauthorizedException extends KrestException {
    public KrestUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

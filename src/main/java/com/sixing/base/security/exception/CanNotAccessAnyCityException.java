package com.sixing.base.security.exception;

import org.springframework.security.core.AuthenticationException;

public class CanNotAccessAnyCityException extends AuthenticationException {

    public CanNotAccessAnyCityException(String msg, Throwable t) {
        super(msg, t);
    }

    public CanNotAccessAnyCityException(String msg) {
        super(msg);
    }
}

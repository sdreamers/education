package com.sixing.base.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码不匹配异常
 *
 * @author Administrator
 * */
public class CaptchaFailException extends AuthenticationException {

    public CaptchaFailException(String msg, Throwable t) {
        super(msg, t);
    }

    public CaptchaFailException(String msg) {
        super(msg);
    }
}

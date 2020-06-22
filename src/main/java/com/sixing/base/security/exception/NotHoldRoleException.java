package com.sixing.base.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 未拥有任何角色异常
 *
 * @author Administrator
 */
public class NotHoldRoleException extends AuthenticationException {

    public NotHoldRoleException(String msg, Throwable t) {
        super(msg, t);
    }

    public NotHoldRoleException(String msg) {
        super(msg);
    }
}

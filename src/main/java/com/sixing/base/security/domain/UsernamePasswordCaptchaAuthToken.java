package com.sixing.base.security.domain;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UsernamePasswordCaptchaAuthToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 500L;
    private final Object principal;
    private Object credentials;
    private Object captcha;

    public UsernamePasswordCaptchaAuthToken(Object principal, Object credentials, Object captcha) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.captcha = captcha;
        this.setAuthenticated(false);
    }

    public UsernamePasswordCaptchaAuthToken(Object principal, Object credentials, Object captcha, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.captcha = captcha;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public Object getCaptcha() {
        return captcha;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
        this.captcha = null;
    }
}

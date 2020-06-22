package com.sixing.base.security.domain;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * security存储的用户实体类
 *
 * @author Administrator
 */
public class User extends AbstractAuthenticationToken {

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户手机号码 对应security的principal
     */
    private String email;

    private String password;

    public User(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.id = id;
        this.email = email;
        this.password = password;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}


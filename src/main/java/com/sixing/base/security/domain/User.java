package com.sixing.base.security.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * security存储的用户实体类
 *
 * @author Administrator
 */
@Getter
@Setter
public class User extends AbstractAuthenticationToken {

    private Long id;
    private String name;
    private String account;
    private String telephone;
    private String password;
    private Integer nature;
    private Integer status;
    private Long supplierId;

    public User(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return account;
    }

    @Override
    public String getName() {
        return name;
    }

}


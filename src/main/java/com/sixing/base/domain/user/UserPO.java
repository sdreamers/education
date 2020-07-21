package com.sixing.base.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPO {

    private Long id;
    private String name;
    private String account;
    private String telephone;
    private String password;
    private Integer nature;
    private Integer status;
    private Long supplierId;
}

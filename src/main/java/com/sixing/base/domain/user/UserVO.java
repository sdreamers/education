package com.sixing.base.domain.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
* 系统用户VO 值对象
* @author 
* @date 2020年7月14日
*/
@Getter
@Setter
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public UserVO() {
	    super();
	}

	private Long id;
	private String name;
	private String account;
	private String telephone;
	private String password;
	private Integer nature;
	private Integer status;
	private Long supplierId;
	private String supplierName;
}
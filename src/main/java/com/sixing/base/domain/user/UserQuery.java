package com.sixing.base.domain.user;

import com.sixing.base.domain.base.BaseVO;
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
public class UserQuery extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public UserQuery() {
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

	private String nameLike;
	private String accountLike;
}
package com.sixing.base.domain.school;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
* 学校VO 值对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class SchoolVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public SchoolVO() {
	    super();
	}

	private Long id;
	private String name;
}
package com.sixing.base.domain.supplier;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
* 供应商VO 值对象
* @author 
* @date 2020年6月22日
*/
@Getter
@Setter
public class SupplierVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public SupplierVO() {
	    super();
	}

	private Long id;
	private String name;
}
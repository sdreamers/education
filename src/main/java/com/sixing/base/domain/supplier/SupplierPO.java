package com.sixing.base.domain.supplier;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
* 供应商的持久对象
* @author 
* @date 2020年6月22日
*/
@Getter
@Setter
public class SupplierPO implements Serializable {
    private static final long serialVersionUID = 1L;

	public SupplierPO() {
        super();
    }

	private Long id;
	private String name;
}
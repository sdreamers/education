package com.sixing.base.domain.supplier;

import com.sixing.base.domain.base.BaseVO;
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
public class SupplierQuery extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public SupplierQuery() {
	    super();
	}

	private Long id;
	private String name;
}
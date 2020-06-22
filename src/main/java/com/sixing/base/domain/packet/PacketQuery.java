package com.sixing.base.domain.packet;

import com.sixing.base.domain.base.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
* 包VO 值对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class PacketQuery extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public PacketQuery() {
	    super();
	}

	private Long id;
	private String name;
	private Long supplierId;
	private String supplierName;
	private Date createTime;
	private String createTimeStart;
    private String createTimeEnd;
	private Long creatorId;
	private String creatorName;
}
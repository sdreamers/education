package com.sixing.base.domain.packet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
* 包的持久对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class PacketPO implements Serializable {
    private static final long serialVersionUID = 1L;

	public PacketPO() {
        super();
    }

	private Long id;
	private String name;
	private Long supplierId;
	private String supplierName;
	private Date createTime;
	private Long creatorId;
	private String creatorName;
}
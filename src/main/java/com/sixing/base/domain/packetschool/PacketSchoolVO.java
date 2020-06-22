package com.sixing.base.domain.packetschool;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 包学校VO 值对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class PacketSchoolVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public PacketSchoolVO() {
	    super();
	}

	private Long id;
	private Long packetId;
	private String packetName;
	private Long schoolId;
	private String schoolName;
	private String currentYear;
	private BigDecimal controlAmount;
}
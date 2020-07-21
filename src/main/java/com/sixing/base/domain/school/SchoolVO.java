package com.sixing.base.domain.school;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
	private Date createTime;
	private Long creatorId;
	private String creatorName;
	private String currentYear;

	private BigDecimal unStartDeviceNumProgress;
	private BigDecimal unStartDeviceAmountProgress;
	private BigDecimal produceDeviceNumProgress;
	private BigDecimal produceDeviceAmountProgress;
	private BigDecimal arrivalDeviceNumProgress;
	private BigDecimal arrivalDeviceAmountProgress;
	private BigDecimal installDeviceNumProgress;
	private BigDecimal installDeviceAmountProgress;
}
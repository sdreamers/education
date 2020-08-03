package com.sixing.base.domain.device;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 设备VO 值对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class DeviceVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public DeviceVO() {
	    super();
	}

	private Long id;
	private Long packetId;
	private String packetName;
	private Long schoolId;
	private String schoolName;
	private String name;
	private String technicalParamter;
	private String specification;
	private String model;
	private String unit;
	private Integer num;
	private BigDecimal excludingTaxPrice;
	private BigDecimal tax;
	private BigDecimal includingTaxPrice;
	/**
	 * 总价 数量*含税单价
	 * */
	private BigDecimal totalAmount;
	private Integer produce;
	private Integer arrival;
	private Integer install;
	private Date produceTime;
	private Date arrivalTime;
	private Date installTime;
	private String currentYear;
}
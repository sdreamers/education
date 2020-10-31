package com.sixing.base.domain.device;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import com.sixing.base.domain.base.BaseVO;
import java.math.BigDecimal;

/**
* 设备VO 值对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class DeviceQuery extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public DeviceQuery() {
	    super();
	}

	private Long id;
	private String no;
	private Long packetId;
	private String packetName;
	private Long schoolId;
	private String schoolName;
	private String name;
	private String nameLike;
	private String technicalParamter;
	private String specification;
	private String model;
	private String unit;
	private Integer num;
	private BigDecimal excludingTaxPrice;
	private BigDecimal tax;
	private Integer inProgressStatusGt;
	private BigDecimal includingTaxPrice;
	private BigDecimal totalAmount;
	private Integer produceNum;
	private Integer arrivalNum;
	private Integer installNum;
	private String currentYear;
	private String brand;
}
package com.sixing.base.domain.device;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设备excel
 *
 * @author sixing
 */
@Data
public class ImportDeviceVO {

    @ExcelProperty("设备名称")
    private String name;

    @ExcelProperty("技术参数")
    private String technicalParamter;

    @ExcelProperty("制造商")
    private String supplier;

    @ExcelProperty("规格")
    private String specification;

    @ExcelProperty("型号")
    private String model;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("数量")
    private Integer num;

    @ExcelProperty("不含税单价")
    private BigDecimal excludingTaxPrice;

    @ExcelProperty("税金")
    private BigDecimal tax;

    @ExcelProperty("含税单价")
    private BigDecimal includingTaxPrice;

    @ExcelProperty("含税合价")
    private BigDecimal totalAmount;

    @ExcelProperty("学校名称")
    private String schoolName;
}

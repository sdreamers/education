package com.sixing.base.domain.device;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 导出设备实体
 *
 * @author hz19104557
 * */
@Data
public class ExportDeviceVO {

    @ExcelProperty(value = "学校名称" ,index = 0)
    private String schoolName;

    @ExcelProperty(value = "设备名称" ,index = 1)
    private String name;

    @ExcelProperty(value = "技术参数" ,index = 2)
    private String technicalParamter;

    @ExcelProperty(value = "规格" ,index = 3)
    private String specification;

    @ExcelProperty(value = "型号" ,index = 4)
    private String model;

    @ExcelProperty(value = "单位" ,index = 5)
    private String unit;

    @ExcelProperty(value = "数量" ,index = 6)
    private Integer num;

    @ExcelProperty(value = "不含税单价" ,index = 7)
    private BigDecimal excludingTaxPrice;

    @ExcelProperty(value = "税金" ,index = 8)
    private BigDecimal tax;

    @ExcelProperty(value = "含税单价" ,index = 9)
    private BigDecimal includingTaxPrice;

    @ExcelProperty(value = "含税总价" ,index = 10)
    private BigDecimal totalAmount;

    @ExcelProperty(value = "当前状态" ,index = 11)
    private String inProgressStatusStr;
}

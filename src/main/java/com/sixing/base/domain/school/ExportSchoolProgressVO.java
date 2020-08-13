package com.sixing.base.domain.school;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 导出学校进度实体
 *
 * @author hz19104557
 */
@Data
public class ExportSchoolProgressVO {

    @ExcelProperty(value = "学校名称", index = 0)
    private String name;
    @ExcelProperty(value = "生产/采购未完成任务进度", index = 1)
    private String unStartDeviceNumProgressStr;

    @ExcelProperty(value = "生产/采购未完成金额进度", index = 2)
    private String unStartDeviceAmountProgressStr;

    @ExcelProperty(value = "生产/采购完成任务进度", index = 3)
    private String produceDeviceNumProgressStr;

    @ExcelProperty(value = "生产/采购完成金额进度", index = 4)
    private String produceDeviceAmountProgressStr;

    @ExcelProperty(value = "到货完成任务进度", index = 5)
    private String arrivalDeviceNumProgressStr;

    @ExcelProperty(value = "到货完成金额进度", index = 6)
    private String arrivalDeviceAmountProgressStr;

    @ExcelProperty(value = "整体任务进度", index = 7)
    private String installDeviceNumProgressStr;

    @ExcelProperty(value = "整体金额进度", index = 8)
    private String installDeviceAmountProgressStr;
}

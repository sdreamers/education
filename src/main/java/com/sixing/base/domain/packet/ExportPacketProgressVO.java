package com.sixing.base.domain.packet;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 导出学校进度实体
 *
 * @author hz19104557
 */
@Data
public class ExportPacketProgressVO {

    @ExcelProperty(value = "包名称", index = 0)
    private String name;

    @ExcelProperty(value = "生产/采购未完成任务进度", index = 1)
    private BigDecimal unStartDeviceNumProgress;

    @ExcelProperty(value = "生产/采购未完成金额进度", index = 2)
    private BigDecimal unStartDeviceAmountProgress;

    @ExcelProperty(value = "生产/采购完成任务进度", index = 3)
    private BigDecimal produceDeviceNumProgress;

    @ExcelProperty(value = "生产/采购完成金额进度", index = 4)
    private BigDecimal produceDeviceAmountProgress;

    @ExcelProperty(value = "到货完成任务进度", index = 5)
    private BigDecimal arrivalDeviceNumProgress;

    @ExcelProperty(value = "到货完成金额进度", index = 6)
    private BigDecimal arrivalDeviceAmountProgress;

    @ExcelProperty(value = "整体任务进度", index = 7)
    private BigDecimal installDeviceNumProgress;

    @ExcelProperty(value = "整体金额进度", index = 8)
    private BigDecimal installDeviceAmountProgress;
}

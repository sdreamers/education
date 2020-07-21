package com.sixing.base.domain.device;

import lombok.Data;

/**
 * 导入设备基本属性
 *
 * @author hz19104557
 * */
@Data
public class ImportDeviceBaseProperties {

    private String packetName;

    private String supplierName;

    private Integer currentYear;
}

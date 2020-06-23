package com.sixing.base.dict.device;

/**
 * 设备类型枚举
 *
 * @author sixing
 * */
public enum DeviceTypeEnum {

    NORMAL(1, "普通"),
    INFORMATION(2, "信息化");

    private Integer code;

    private String name;

    DeviceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

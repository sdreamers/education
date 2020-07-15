package com.sixing.base.dict.device;

import java.util.Arrays;
import java.util.Optional;

/**
 * 正在进行中的状态枚举
 *
 * @author sixing
 * */
public enum DeviceStatusEnum {

    PRODUCING(0, "未完成"),
    ARRIVING(1, "已完成"),
    ;


    private Integer code;

    private String name;

    DeviceStatusEnum(Integer code, String name) {
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

    public static String findName(Integer code) {
        Optional<DeviceStatusEnum> optional = Arrays.stream(DeviceStatusEnum.values()).filter(item -> item.getCode() == code).findFirst();
        return optional.map(DeviceStatusEnum::getName).orElse(null);
    }
}

package com.sixing.base.dict.device;

import java.util.Arrays;
import java.util.Optional;

/**
 * 正在进行中的状态枚举
 *
 * @author sixing
 * */
public enum InProgressStatusEnum {

    PRODUCING(1, "生产中"),
    ARRIVING(2, "到货中"),
    INSTALLING(3, "安装中"),
    INFORMATION_ARRIVING(4, "到货中"),
    INFORMATION_INSTALLING(5, "安装中"),
    INFORMATION_DEBUGGING(6, "调试中")
    ;


    private Integer code;

    private String name;

    InProgressStatusEnum(Integer code, String name) {
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
        Optional<InProgressStatusEnum> optional = Arrays.stream(InProgressStatusEnum.values()).filter(item -> item.getCode() == code).findFirst();
        return optional.map(InProgressStatusEnum::getName).orElse(null);
    }
}

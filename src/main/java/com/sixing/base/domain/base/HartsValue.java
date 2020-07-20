package com.sixing.base.domain.base;

import java.math.BigDecimal;
import java.util.List;

public class HartsValue {

    private Integer valueType;


    private List<BigDecimal> value;

    public HartsValue(Integer valueType, List<BigDecimal> value) {
        this.valueType = valueType;
        this.value = value;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public List<BigDecimal> getValue() {
        return value;
    }

    public void setValue(List<BigDecimal> value) {
        this.value = value;
    }
}

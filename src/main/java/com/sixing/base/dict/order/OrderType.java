package com.sixing.base.dict.order;

import com.sixing.base.utils.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderType {

    public static final Map<String, String> DICT             = new LinkedHashMap<String, String>();

    public static final String INCOME_KEY = "1";
    public static final String INCOME_VALUE = "收入";

    public static final String EXPEND_KEY = "2";
    public static final String EXPEND_VALUE = "支出";


    public static final OrderType INCOME      = new OrderType(INCOME_KEY, INCOME_VALUE);
    public static final OrderType EXPEND = new OrderType(EXPEND_KEY, EXPEND_VALUE);

    private String                          key;
    private String                          value;

    private OrderType() {
        super();
    }

    private OrderType(String key, String value) {
        super();
        this.key = key;
        this.value = value;
        set(key, value);
    }

    public static String get(String key) {
        return DICT.get(key);
    }

    public static void set(String key, String value) {
        DICT.put(key, value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getKey(String value) {
        String key = null;
        Iterator<Map.Entry<String, String>> its = DICT.entrySet().iterator();
        while (its.hasNext()) {
            Map.Entry<String, String> entry = its.next();
            if (StringUtils.equals(entry.getValue(), value)) {
                key = (String) entry.getKey();
                break;
            }
        }
        return key;
    }


}

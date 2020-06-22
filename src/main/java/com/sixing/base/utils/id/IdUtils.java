package com.sixing.base.utils.id;

import com.sixing.base.utils.log.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * ID工具类
 */
@Component
public class IdUtils {
    
    @Autowired
    private LogUtils log;

    /** 生产者ID */
    public static Long PROVIDER_ID = null;
    /** 数据中心ID */
    public static Long DATA_CENTER_ID = null;

    /**下单id前缀*/
    public static final String ID_PREFIX_ORDER = "OD";
    /**充值id前缀*/
    public static final String ID_PREFIX_RECHARGE_ORDER = "RO";
    /**退货id前缀*/
    public static final String ID_PREFIX_REFUND_ORDER = "RF";
    /**大数据id前缀*/
    public static final String ID_PREFIX_BIG_DATA = "BD";
    /** 同类商品 id前缀 */
    public static final String ID_PREFIX_SIMILAR_PRODUCT = "SP";

    /**
     * 获得当前生产者ID
     * 
     * @return 生产者ID
     */
    public static long getProviderId() {
        if (PROVIDER_ID == null) {
            synchronized (IdUtils.class) {
                if (PROVIDER_ID == null) {
                    try {
                        PROVIDER_ID = Long.parseLong(ResourceBundle.getBundle("common").getString("idworker.providerid"));
                    }
                    catch (Exception ex) {
                        PROVIDER_ID = 0L;
                    }
                }
            }
        }
        return PROVIDER_ID;
    }

    /**
     * 获得当前数据中心ID
     * 
     * @return 数据中心ID
     */
    public static long getDataCenterId() {
        if (DATA_CENTER_ID == null) {
            synchronized (IdUtils.class) {
                if (DATA_CENTER_ID == null) {
                    try {
                        DATA_CENTER_ID = Long.parseLong(ResourceBundle.getBundle("common").getString("idworker.datacenterid"));
                    }
                    catch (Exception ex) {
                        DATA_CENTER_ID = 0L;
                    }
                }
            }
        }
        return DATA_CENTER_ID;
    }

    /**
     * 生成雪花ID
     * @return
     */
    public static long generateId() {
        return IdWorker.generateId(getProviderId(), getDataCenterId());
    }
}
package com.sixing.base.constant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态变量
 * 
 */
public class Constant {
    /*
     */
    public static final int RESPONSE_UNBIND_WECHANT_CODE = 90;
	/*RESPONSE_ORDER_ERROR_CODE = 97 订单取消失败的错误信息*/
	public static final int RESPONSE_ORDER_ERROR_CODE = 97;
    public static final int RESPONSE_BUSINESS_ERROR_CODE = 98;
    public static final int RESPONSE_ERROR_CODE = 99;
    public static final int RESPONSE_SUCCESS_CODE = 100;
    /**商户存在code*/
    public static final int RESPONSE_EXIST_MERCHANT_CODE = 96;
    /**配送员扫码已送达订单*/
    public static final int RESPONSE_DELIVERY_ORDER = 95;
    /**订单支付超时*/
    public static final int RESPONSE_ORDER_PAY_TIMEOUT = 94;
    /**微信绑定错误重新登录绑定*/
    public static final int RESPONSE_BIND_WECHANT_ERRO_CODE = 91;

    /**APP可充值最大值*/
    public static final BigDecimal APP_RECHARGE_MAX_AMOUNT = new BigDecimal(9999999.99);

    public static final String DIRECTION_ASC = "asc";
    public static final String DIRECTION_DESC = "desc";

    /* 百度地图 */
    public static final String geoconv = "http://api.map.baidu.com/geoconv/v1/?";
    public static final String baiduAk = "VsL4GdnSY6CLxffpQnLIqWOldNrMmIrj";

    /**
     * 信鸽推送账户accessId
     */
    public final static int XG_ACCESS_ID = 2100267005;
    
    /**
     * 信鸽推送账户SECRET KEY
     */
    public final static String SECRET_KEY = "c0cc384fa19b89b822b84fbc3342d48b";

    /**
     * 通用
     */
    public static final String pageSize = "10";
    public static final String userDefaultAddressFlag = "1";
    public static final String userUnDefaultAddressFlag = "0";

    /** 记录标语模板 */
    public static final String RECORD_ALI_OR_WX_PAY_PREFIX = "您使用第三方支付消费了";
    public static final String RECORD_YUE_PAY_PREFIX = "您使用余额消费了";
    public static final String RECORD_COD_PAY_PREFIX = "您使用货到付款消费了";
    public static final String RECORD_PAY_SUFFIX = "元,经验值+";
    public static final String RECORD_ALI_OR_WX_CANCEL_ORDER_PREFIX = "您取消了使用第三方支付的订单,经验值-";
    public static final String RECORD_YUE_CANCEL_ORDER_PREFIX = "您取消了使用余额支付的订单,经验值-";
    public static final String RECORD_COD_CANCEL_ORDER_PREFIX = "您取消了使用货到付款的订单,经验值-";

    /** 商户消息通知 */
    public static final String RECEIVED_THE_LOGISTICS_NOTIFICATION_TO_DPY = "收到来至冻品云的物流通知";
    public static final String PAYMENT_NOTICE_TO_DPY = "收到来至冻品云的支付通知";
    public static final String RECEIVED_A_REFUND_NOTICE_TO_DPY = "收到来至冻品云的退款通知";
    public static final String RECEIVED_THE_ARRIVAL_NOTICE_OF_DPY = "收到来至冻品云的到货通知";
    public static final String RECEIVED_PROMOTION_MESSAGE_TO_DPY = "收到来至冻品云的到货通知";

    /**
     * 商户地址信息审核状态
     */
    /**
     * 待审核
     */
    public static final String ADDRESS_DESCRIPTION_WAITING = "0";
    /**
     * 审核通过
     */
    public static final String ADDRESS_DESCRIPTION_APPROVED = "1";
    /**
     * 审核拒绝
     */
    public static final String ADDRESS_DESCRIPTION_REJECTED = "2";
    public static final Map<String, String> addressDescriptionMap = new HashMap<String, String>();
    static {
        addressDescriptionMap.put(ADDRESS_DESCRIPTION_WAITING, "待审核");
        addressDescriptionMap.put(ADDRESS_DESCRIPTION_APPROVED, "审核通过");
        addressDescriptionMap.put(ADDRESS_DESCRIPTION_REJECTED, "审核拒绝");
    }

    /* payment表支付交易类型 */
    /**
     * 订单支付
     */
    public static final String ORDER_PAYMENT = "1";
    /**
     * 充值支付
     */
    public static final String RECHARGE_PAYMENT = "2";
    /**
     * 退款
     */
    public static final String ORDER_REFUND_PAYMENT = "3";
    /**
     * 退款时扣除积分，积分不足，用退款金额抵扣
     */
    public static final String ORDER_REFUND_DEDUCT_POINTS_AND_PRICE = "4";
    public static final Map<String, String> paymentTypeMap = new HashMap<String, String>();
    static {
        paymentTypeMap.put(ORDER_PAYMENT, "订单支付");
        paymentTypeMap.put(RECHARGE_PAYMENT, "充值支付");
        paymentTypeMap.put(ORDER_REFUND_PAYMENT, "订单退款");
        paymentTypeMap.put(ORDER_REFUND_DEDUCT_POINTS_AND_PRICE, "退款积分不足扣金额");
    }

    /* 订单状态请求参数 */
    /** 未支付订单 */
    public static final String UNPAID = "unpaid";
    /** 未发货订单 */
    public static final String UNDELIVERED = "undelivered";
    /** 未送达订单 */
    public static final String UNRECEIVED = "unreceived";
    /** 已完成订单 */
    public static final String COMPLETED = "completed";
    /** 待评价订单 */
    public static final String UNEVALUATED = "unevaluated";
    /** 售后状态订单 */
    public static final String AFTERSALESERVICE = "afterSaleService";
    /** 合并未发货和未送达订单 */
    public static final String UNDELIVERED_UNITE_UNRECEIVED = "undeliveredUniteUnreceived";

    /* 订单审核状态 */
    /** 等待过账 */
    public static final String ORDER_POST_WAIT = "0";
    /** 已过账 */
    public static final String ORDER_POST = "1";

    /* 订单过账审核状态 */
    /** 等待审核 */
    public static final String ORDER_POST_REVIEW_WAIT = "0";
    /** 审核通过 */
    public static final String ORDER_POST_REVIEW_APPROVED = "1";

    /* 配送员状态 */
    /** 离线状态 */
    public static final String DELIVERYMAN_OFFLINE = "0";
    /** 空闲中 */
    public static final String DELIVERYMAN_AVAILABLE = "1";
    /** 正在运送途中 */
    public static final String DELIVERYMAN_SHIPPING = "2";

    public static final Map<String, String> deliverymanStatusMap = new HashMap<String, String>();
    static {
        deliverymanStatusMap.put(DELIVERYMAN_OFFLINE, "离线");
        deliverymanStatusMap.put(DELIVERYMAN_AVAILABLE, "空闲中");
        deliverymanStatusMap.put(DELIVERYMAN_SHIPPING, "配送中");
    }
    /* 业务员状态 */
    /** 离线状态 */
    public static final String SALESMAN_OFFLINE = "0";
    /** 空闲中 */
    public static final String SALESMAN_AVAILABLE = "1";
    /** 正在运送途中 */
    public static final String SALESMAN_SHIPPING = "2";
    public static final Map<String, String> salesmanStatusMap = new HashMap<String, String>();
    static {
        salesmanStatusMap.put(SALESMAN_OFFLINE, "离线");
        salesmanStatusMap.put(SALESMAN_AVAILABLE, "空闲中");
        salesmanStatusMap.put(SALESMAN_SHIPPING, "配送中");
    }

    /* 配送员性别 */
    public static final String DELIVERYMAN_SEX_MALE = "m";
    public static final String DELIVERYMAN_SEX_FEMALE = "f";
    public static final Map<String, String> deliverymanSexMap = new HashMap<String, String>();
    static {
        deliverymanSexMap.put(DELIVERYMAN_SEX_FEMALE, "女");
        deliverymanSexMap.put(DELIVERYMAN_SEX_MALE, "男");
    }
    /* 业务员性别 */
    public static final String SALESMAN_SEX_MALE = "m";
    public static final String SALESMAN_SEX_FEMALE = "f";
    public static final Map<String, String> salesmanSexMap = new HashMap<String, String>();
    static {
        salesmanSexMap.put(SALESMAN_SEX_FEMALE, "女");
        salesmanSexMap.put(SALESMAN_SEX_MALE, "男");
    }

    /* 商户认证状态 */
    /** 未认证 */
    public static final String MERCHANT_UN_AUTHORIZED = "0";
    /** 已认证 */
    public static final String MERCHANT_AUTHORIZED = "1";
    /** 等待认证结果 */
    public static final String MERCHANT_WAITING_VERIFICATION = "2";
    /** 认证失败 */
    public static final String MERCHANT_AUTHORIZE_FAILED = "3";
    public static final Map<String, String> merchantStatusMap = new HashMap<String, String>();
    static {
        merchantStatusMap.put(MERCHANT_AUTHORIZED, "通过认证");
        merchantStatusMap.put(MERCHANT_UN_AUTHORIZED, "未认证");
        merchantStatusMap.put(MERCHANT_WAITING_VERIFICATION, "待认证");
        merchantStatusMap.put(MERCHANT_AUTHORIZE_FAILED, "认证失败");
    }

    /* 商户货到付款权限 */
    public static final String COD_PRIVILAGE_ON = "1";
    public static final String COD_PRIVILAGE_OFF = "0";

    /* 申请退货状态 */
    public static final String ORDER_REFUND_WAITING = "0";
    public static final String ORDER_REFUND_APPROVED = "1";
    public static final String ORDER_REFUND_REJECTED = "2";
    public static final Map<String, String> orderRefundStatusMap = new HashMap<String, String>();
    static {
        orderRefundStatusMap.put(ORDER_REFUND_WAITING, "待审核");
        orderRefundStatusMap.put(ORDER_REFUND_APPROVED, "审核通过");
        orderRefundStatusMap.put(ORDER_REFUND_REJECTED, "审核驳回");
    }

    /*退货原因*/
    /**无货*/
    public static final String RETURN_CAUSE_NO_GOODS = "0"; 
    /**不想买啦*/
    public static final String RETURN_CAUSE_NOT_BUY = "1"; 
    /**质量有问题*/
    public static final String RETURN_CAUSE_DELIVERED_QUALITY_PROBLEM = "2"; 
    /**其他*/
    public static final String RETURN_CAUSE_OTHER = "3"; 
    public static final Map<String, String> returnCauseMap = new HashMap<String, String>();
    static {
    	returnCauseMap.put(RETURN_CAUSE_NO_GOODS, "无货");
    	returnCauseMap.put(RETURN_CAUSE_NOT_BUY, "不想买啦");
    	returnCauseMap.put(RETURN_CAUSE_DELIVERED_QUALITY_PROBLEM, "质量有问题");
    	returnCauseMap.put(RETURN_CAUSE_OTHER, "其他");
    }
    
    /**
     * 首页导出Excel请求的参数值
     */
    public static final String BUSINESS_EXCEL = "business";

    public static final String RECHARGE_EXCEL = "recharge";

    public static final String BEFORE_DELIVERY_EXCEL = "beforeDelivery";

    public static final String SALES_VOLUME_EXCEL = "salesVolume";

    /**
     * 采购计划导出
     */
    public static final String PURCHASING_PLAN_EXCEL = "PurchasingPlanExcel";
    /**
     * 商户默认地址
     */
    public static final String DEFAULT_ADDRESS_FLAG = "1";


    /* 消息类型 */
    public static final String SYSTEM_MESSAGE = "1";
    public static final Map<String, String> systemMessageTypeMap = new HashMap<String, String>();
    static {
        systemMessageTypeMap.put(SYSTEM_MESSAGE, "系统信息");

    }
    /* 系统消息发送状态 */
    /** 未发送 */
    public static final String MESSAGE_SEND_ON = "1";
    /** 已发送 */
    public static final String MESSAGE_SEND_OFF = "2";

    /* redis短信验证码超时时间 */
    public static final int MSGCODE_EXPIRE_TIME = 120;
    public static final int MSGCODE_RESENT_WAITING_TIME = 60;

    /* redis短信用户登录*/
    public static final String USER_LOGIN_MSG_CODE = "user_login_";
    /* redis collection相关key名称 */
    public static final String MERCHANT_REGISTER_KEY_PREFIX = "merchant_register_";
    public static final String MERCHANT_RESET_PASSWORD_PREFIX = "merchant_reset_";

    public static final String MERCHANT_BIND_WX_PREFIX = "merchant_bind_wx";

    /* delivery_address表中defaultFlag */
    public static final String DELIVERY_ADDRESS_DEFAULT_FLAG = "1";
    public static final String DELIVERY_ADDRESS_NOT_DEFAULT_FLAG = "0";

    /**
     * 商品系统认定最大价格
     */
    public static final Double PRODUCT_MAX_PRICE = 999999999.0;

    // 库存操作
    /** 入库 */
    public static final String IN_STOCK = "1";
    /** 出库 */
    public static final String OUT_STOCK = "2";
    /** 盘盈 */
    public static final String INVENTORY_PROFIT = "3";
    /** 盘亏 */
    public static final String INVENTORY_LOSS = "4";

    public static final Map<String, String> inOutStockTypeMap = new HashMap<String, String>();
    static {
        inOutStockTypeMap.put(IN_STOCK, "入库");
        inOutStockTypeMap.put(OUT_STOCK, "出库");
    }
    // 入库操作子类型
    /** 进货 */
    public static final String IN_STOCK_UP = "in_stock_up";
    /** 盘盈 */
    public static final String IN_INVENTORY_PROFIT = "in_inventory_profit";
    /** 商户退货 */
    public static final String IN_REFUNDS = "in_refunds";
    /** 售出 */
    public static final String OUT_SOLD_OUT = "out_sold_out";
    /** 盘亏 */
    public static final String OUT_INVENTORY_LOSS = "out_inventory_loss";
    /** 损耗 */
    public static final String OUT_WASTAGE = "out_wastage";

    public static final Map<String, String> inOutStockSubTypeMap = new HashMap<String, String>();
    static {
        inOutStockSubTypeMap.put(IN_STOCK_UP, "进货");
        inOutStockSubTypeMap.put(IN_INVENTORY_PROFIT, "盘盈");
        inOutStockSubTypeMap.put(IN_REFUNDS, "商户退货");
        inOutStockSubTypeMap.put(OUT_SOLD_OUT, "售出");
        inOutStockSubTypeMap.put(OUT_INVENTORY_LOSS, "盘亏");
        inOutStockSubTypeMap.put(OUT_WASTAGE, "损耗");
    }
    public static final Map<String, String> inOutStockDataSubTypeMap = new HashMap<String, String>();
    static {
    	inOutStockDataSubTypeMap.put(IN_STOCK_UP, "进货");
    	inOutStockDataSubTypeMap.put(IN_INVENTORY_PROFIT, "盘盈");
    	inOutStockDataSubTypeMap.put(OUT_INVENTORY_LOSS, "盘亏");
    	inOutStockDataSubTypeMap.put(OUT_WASTAGE, "损耗");
        inOutStockDataSubTypeMap.put(IN_REFUNDS, "退货");
    }

    public static final Map<String, String> purchasePaymentMethodMap = new HashMap<String, String>();
    static {
        purchasePaymentMethodMap.put("0", "现金");
        purchasePaymentMethodMap.put("1", "信用");
        purchasePaymentMethodMap.put("2", "转账");
        purchasePaymentMethodMap.put("3", "抵贷款");
    }

    // 权限分类
    /** 菜单 */
    public static final String PERMISSION_TYPE_DIRECTORY = "0";
    /** 页面 */
    public static final String PERMISSION_TYPE_MENU = "1";
    /** 功能 */
    public static final String PERMISSION_TYPE_BUTTON = "2";
    public static final Map<String, String> permissionTypeMap = new HashMap<String, String>();
    static {
        permissionTypeMap.put(PERMISSION_TYPE_DIRECTORY, "菜单");
        permissionTypeMap.put(PERMISSION_TYPE_MENU, "页面");
        permissionTypeMap.put(PERMISSION_TYPE_BUTTON, "功能");
    }

    /**可选预约时间系数：默认0 表示当天，1表示明天，以此类推*/
    public static final Integer DEFAULT_INTERVAL_COEFFICIENT = 0;

    /**品牌专区分组所在的分类id*/
    public static final int BRAND_CATEGORY_ID = -1;
    /**菜系专区分组所在分类id*/
    public static final int MERCHANT_TYPE_CATEGORY_ID = -2;

    /*塞选时间范围段限制天数*/
    public static final int LIMITED_DAYS = 30;

    /**收货人姓名最大长度*/
    public static final int CONSIGNEE_NAME_MAX_LENGHT = 10;
}
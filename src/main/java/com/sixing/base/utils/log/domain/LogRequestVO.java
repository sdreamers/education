package com.sixing.base.utils.log.domain;

import com.sixing.base.utils.JsonUtils;

/**
 * 接口请求日志的值对象
 */
public class LogRequestVO {

    private String url;            // 请求网址
    private String param;          // 请求入参
    private String paramBody;      // RequestBody请求入参
    private String method;         // 请求方式
    private String agent;          // 请求者的终端信息
    private String ip;             // 请求者的IP
    private String responseTime;   // 响应耗时，单位：毫秒
    private String createDateTime; // 创建日期时间
    private String userType;       // 请求者的用户类型
    private String userId;         // 请求者的用户ID
    private String userTelephone;  // 请求者的用户手机号

    public LogRequestVO() {
        super();
    }

    public LogRequestVO(String url, String param, String paramBody, String method, String agent, String ip, String responseTime, String createDateTime) {
        super();
        this.url = url;
        this.param = param;
        this.paramBody = paramBody;
        this.method = method;
        this.agent = agent;
        this.ip = ip;
        this.responseTime = responseTime;
        this.createDateTime = createDateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParamBody() {
        return paramBody;
    }

    public void setParamBody(String paramBody) {
        this.paramBody = paramBody;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserTelephone() {
        return userTelephone;
    }

    public void setUserTelephone(String userTelephone) {
        this.userTelephone = userTelephone;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }

}
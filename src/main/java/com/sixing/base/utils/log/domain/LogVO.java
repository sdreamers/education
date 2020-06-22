package com.sixing.base.utils.log.domain;

import com.sixing.base.utils.JsonUtils;

import java.io.Serializable;

/**
 * 日志的值对象
 */
public class LogVO implements Serializable {

    private static final long serialVersionUID = -29798067226136543L;
    private String            rowKey;                                // 行键（相当于关系数据库的字段ID）
    private String            project;                               // 系统代码
    private String            code;                                  // 日志级别
    private String            message;                               // 日志内容
    private String            createDateTime;                        // 创建日期时间

    public LogVO() {
        super();
    }

    public LogVO(String rowKey, String project, String code, String message, String createDateTime) {
        super();
        this.rowKey = rowKey;
        this.project = project;
        this.code = code;
        this.message = message;
        this.createDateTime = createDateTime;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}

package com.sixing.base.domain.base;

import java.io.Serializable;

/**
 * 热力图
 * */
public class HartsResult implements Serializable {

    /* 数据 */
    private Object data;
    /* X数据 */
    private Object X;
    /* Y数据 */
    private Object Y;

    private int code = 100;

    private String message;

    public HartsResult() { }

    public HartsResult(Object data, Object X, Object Y) {
        this.data = data;
        this.X = X;
        this.Y = Y;
    }

    public HartsResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HartsResult ok(Object data, Object X, Object Y) {
        return new HartsResult(data, X, Y);
    }

    public static HartsResult error(String message) {
        return new HartsResult(99, message);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getX() {
        return X;
    }

    public void setX(Object x) {
        X = x;
    }

    public Object getY() {
        return Y;
    }

    public void setY(Object y) {
        Y = y;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

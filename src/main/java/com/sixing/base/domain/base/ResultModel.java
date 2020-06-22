package com.sixing.base.domain.base;

import com.sixing.base.constant.Constant;
import com.sixing.base.constant.ResponseMsgConstant;

/**
 * 自定义返回结果
 * 
 * @author XieEnlong
 * @date 2015/7/14.
 */
public class ResultModel<T> {

	/**
	 * 返回码
	 */
	private int code;

	/**
	 * 返回结果描述
	 */
	private String message;

	/**
	 * 返回内容
	 */
	private T content;

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

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	public ResultModel() {

	}

	public ResultModel(int code, String msg) {
		this.code = code;
		this.message = msg;
	}

	public ResultModel(int code, String msg, T content) {
		this.code = code;
		this.message = msg;
		this.content = content;
	}
	
	public static <T> ResultModel<T> ok() {
		return new ResultModel<T>(Constant.RESPONSE_SUCCESS_CODE, ResponseMsgConstant.success);
	}
	
	public static <T> ResultModel<T> ok(T content) {
		return new ResultModel<T>(Constant.RESPONSE_SUCCESS_CODE, ResponseMsgConstant.success, content);
	}

	public static ResultModel fail(int code, String message) {
		return new ResultModel(code, message);
	}

	public static <T> ResultModel<T> fail(String message) {
		return new ResultModel<T>(Constant.RESPONSE_ERROR_CODE, message);
	}
}

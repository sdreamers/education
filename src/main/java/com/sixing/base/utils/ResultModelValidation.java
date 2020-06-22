package com.sixing.base.utils;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.utils.exception.ServiceException;

/**
 * 跨服务端的数据传输对象异常校验类
 *
 * @author Administrator
 */
public class ResultModelValidation {
    /**
     * 校验远程调用是否异常
     *
     * @param resultModel 跨服务端的数据传输返回对象
     * @throws ServiceException 业务异常
     */
    public static void checkNull(ResultModel<?> resultModel) throws ServiceException {
        if (resultModel == null) {
            throw new ServiceException("远程调用异常，请重试");
        }
    }

    /**
     * 校验远程调用是否异常
     *
     * @param resultModel 跨服务端的数据传输返回对象
     * @throws ServiceException 业务异常
     */
    public static void checkError(ResultModel<?> resultModel) throws ServiceException {
        checkNull(resultModel);
        if (resultModel.getCode() != Constant.RESPONSE_SUCCESS_CODE) {
            throw new ServiceException(resultModel.getCode(), resultModel.getMessage());
        }
    }

    /**
     * 校验远程调用异常
     *
     * @param resultModel 跨服务端的数据传输返回对象
     * @param message 自定义返回信息
     * @throws ServiceException 业务异常
     */
    public static void checkError(ResultModel<?> resultModel, String message) throws ServiceException {
        checkNull(resultModel);
        if (resultModel.getCode() != Constant.RESPONSE_SUCCESS_CODE) {
            throw new ServiceException(resultModel.getCode(), message);
        }
    }

    /**
     * 校验远程调用异常
     *
     * @param resultModel 跨服务端的数据传输返回对象
     * @param code 自定义错误码
     * @param message 自定义返回信息
     * @throws ServiceException 业务异常
     */
    public static void checkError(ResultModel<?> resultModel, int code, String message) throws ServiceException {
        checkNull(resultModel);
        if (resultModel.getCode() != Constant.RESPONSE_SUCCESS_CODE) {
            throw new ServiceException(code, message);
        }
    }
}

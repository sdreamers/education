package com.sixing.base.security.handler;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.utils.JsonUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 权限不足，拒绝访问回调
 *
 * @author Administrator
 * */
@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        ResultModel<String> resultModel = new ResultModel<>();
        resultModel.setCode(Constant.RESPONSE_ERROR_CODE);
        resultModel.setMessage("权限不足, 请联系管理员");
        out.write(JsonUtils.toJson(resultModel));
        out.flush();
        out.close();
    }
}

package com.sixing.base.security.handler;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.utils.JsonUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录失败的回调
 *
 * @author Administrator
 * */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        ResultModel<String> resultModel = new ResultModel<>();
        resultModel.setCode(Constant.RESPONSE_ERROR_CODE);
        resultModel.setMessage(exception.getMessage());
        out.write(JsonUtils.toJson(resultModel));
        out.flush();
        out.close();
    }
}

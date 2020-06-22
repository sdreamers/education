package com.sixing.base.security.handler;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.utils.CookieUtils;
import com.sixing.base.utils.JsonUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 退出登录成功回调
 *
 * @author Administrator
 * */
@Component
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CookieUtils.delCookie(request, response, "cityId");
        CookieUtils.delCookie(request, response, "cityName");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        ResultModel<String> resultModel = new ResultModel<>();
        resultModel.setCode(Constant.RESPONSE_SUCCESS_CODE);
        resultModel.setMessage("退出登录成功");
        out.write(JsonUtils.toJson(resultModel));
        out.flush();
        out.close();
    }
}

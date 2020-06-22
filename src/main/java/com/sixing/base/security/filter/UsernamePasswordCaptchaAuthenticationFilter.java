package com.sixing.base.security.filter;

import com.sixing.base.security.domain.UsernamePasswordCaptchaAuthToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UsernamePasswordCaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private boolean postOnly = true;
    private String captchaParameter = "captcha";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String username = this.obtainUsername(request);
            String password = this.obtainPassword(request);
            String captcha = this.obtainCaptcha(request);
            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            if (captcha == null) {
                captcha = "";
            }

            username = username.trim();
            UsernamePasswordCaptchaAuthToken authToken = new UsernamePasswordCaptchaAuthToken(username, password, captcha);
            this.setDetails(request, authToken);
            return this.getAuthenticationManager().authenticate(authToken);
        }
    }

    private String obtainCaptcha(HttpServletRequest request) {
        return request.getParameter(this.captchaParameter);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordCaptchaAuthToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setCaptchaParameter(String captchaParameter) {
        this.captchaParameter = captchaParameter;
    }
}

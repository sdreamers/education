package com.sixing.base.security.filter;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局跨域过滤器, 放在security过滤器链与ZuulFilter之前
 *
 * @author Administrator
 */
public class SimpleCrossDomainFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = request.getHeader("Referer");
        }
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if (RequestMethod.OPTIONS.toString().equals(request.getMethod())) {
            String allowMethod = request.getHeader("Access-Control-Request-Method");
            String allowHeaders = request.getHeader("Access-Control-Request-Headers");
            // 浏览器缓存预检请求结果时间,单位:秒
            response.setHeader("Access-Control-Max-Age", "86400");
            // 允许浏览器在预检请求成功之后发送的实际请求方法名
            response.setHeader("Access-Control-Allow-Methods", allowMethod);
            // 允许浏览器发送的请求消息头
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

package com.sixing.base.utils;

import com.sixing.base.utils.exception.ServiceException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * 对cookie的增删改查操作
 * @author Administrator
 *
 */
public class CookieUtils {
    
    /**
     * 读取指定key的cookie
     * 注意二、从客户端读取Cookie时，包括maxAge在内的其他属性都是不可读的，也不会被提交。浏览器提交Cookie时只会提交name与value属性。maxAge属性只被浏览器用来判断Cookie是否过期
     * @param request
     * @param name
     */
    public static String showCookieByName(HttpServletRequest request, String name) throws ServiceException {
        try {            
            String value = null;
            Cookie[] cookies = request.getCookies();
            if (CollectionUtils.isNotEmpty(cookies)) {
                for (Cookie cookie : cookies) {
                    if (StringUtils.equals(cookie.getName(), name)) {
                        value = cookie.getValue();
                    }
                }
            }
            return value;
        }
        catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
    
    /**
     * 添加cookie
     * @param response
     * @param name
     * @param value
     */
    public static void addCookie(HttpServletResponse response, String name, String value){
        
        try {
        	value = URLEncoder.encode(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        Cookie cookie = new Cookie(name.trim(), value.trim());
        // 设置为30min
        cookie.setMaxAge(30*24*60*60);
        cookie.setPath("/");
        
        response.addCookie(cookie);
    }
    /**
     * 修改cookie
     * @param request
     * @param response
     * @param name
     * @param value
     * 注意一、修改、删除Cookie时，新建的Cookie除value、maxAge之外的所有属性，例如name、path、domain等，都要与原Cookie完全一样。否则，浏览器将视为两个不同的Cookie不予覆盖，导致修改、删除失败。
     */
    public static void editCookie(HttpServletRequest request, HttpServletResponse response, String name, String value){
        Cookie[] cookies = request.getCookies();
        if (null==cookies) {
        } else {
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    cookie.setValue(value);
                    cookie.setPath("/");
                    cookie.setMaxAge(30 * 60);// 设置为30min
                    response.addCookie(cookie);
                    break;
                }
            }
        }
         
    }
    /**
     * 删除cookie
     * @param request
     * @param response
     * @param name
     */
    public static void delCookie(HttpServletRequest request, HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies();
        if (null==cookies) {
        } else {
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    cookie.setValue(null);
                    cookie.setMaxAge(0);// 立即销毁cookie
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }

    public static Integer getCurrentCityId(HttpServletRequest request, HttpServletResponse response) {
        Integer cityId = null;
        try {
            String cityIdStr = CookieUtils.showCookieByName(request, "cityId");
            if (StringUtils.isNumeric(cityIdStr)) {
                cityId = Integer.parseInt(cityIdStr);
            }
        }
        catch (ServiceException e) {

        }
        return cityId;
    }

    public static Integer getCurrentCityId(HttpServletRequest request) {
        Integer cityId = null;
        try {
            String cityIdStr = CookieUtils.showCookieByName(request, "cityId");
            if (StringUtils.isNumeric(cityIdStr)) {
                cityId = Integer.parseInt(cityIdStr);
            }
        }
        catch (ServiceException e) {

        }
        return cityId;
    }

    public static String quotaYear(HttpServletRequest request) {
        try {
            return CookieUtils.showCookieByName(request, "quotaYear");
        } catch (ServiceException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void test() {
    	Properties p = new Properties();
    	InputStream is = CookieUtils.class.getResourceAsStream("/comment.properties");
    	try {
			p.load(is);
			is.close();
			String s = p.getProperty("cookie.scope");
			System.out.println("********"+s);
		} catch (IOException e) {
			System.out.println("没有");
		}

	}
	
}

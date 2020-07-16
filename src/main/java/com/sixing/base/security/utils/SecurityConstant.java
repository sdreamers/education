package com.sixing.base.security.utils;

public class SecurityConstant {

    /**
     * 角色前缀
     * */
    public final static String ROLE_PREFIX = "ROLE_";

    /**
     * 虚拟角色，任何登陆用户都拥有该角色
     * */
    public static final String HAS_AUTHENTICATED_KEY = "has_authenticated";

    /**
     * ERP url 前缀
     * */
    public static final String KEY_ADMIN_PREFIX = "/admin";

    /**
     * 后台登陆用户名参数名
     * */
    public static final String KEY_USERNAME = "account";

    /**
     * 后台登陆密码参数名
     * */
    public static final String KEY_PASSWORD = "password";

    /**
     * 后台登陆验证码参数名
     * */
    public static final String KEY_CAPTCHA = "captcha";

    /**
     * 后台登陆记住我参数名
     * */
    public static final String KEY_REMEMBER_ME = "rememberMe";
}

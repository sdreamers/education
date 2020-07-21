package com.sixing.base.security.utils;

import com.sixing.base.security.domain.User;
import com.sixing.base.utils.exception.ServiceException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * token管理器
 *
 * @author sixing
 * */
public class TokenManager {

    public static Long getUserId() throws ServiceException{
        User user = getUser();
        return user.getId();
    }

    public static User getUser() throws ServiceException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication();
        if (user == null) {
            throw new ServiceException("登录超时, 请刷新页面");
        }
        return user;
    }
}
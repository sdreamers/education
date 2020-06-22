package com.sixing.base.security.utils;

import com.sixing.base.security.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenManager {

    public static Long getUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication();
        return user.getId();
    }
}
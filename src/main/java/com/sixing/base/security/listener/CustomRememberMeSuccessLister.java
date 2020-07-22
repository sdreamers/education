package com.sixing.base.security.listener;

import com.sixing.base.domain.user.UserPO;
import com.sixing.base.security.domain.User;
import com.sixing.base.utils.BeanCopierUtil;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 观察者模式 监听remember me登录成功的消息事件
 *
 * @author Administrator
 * */
@Component
public class CustomRememberMeSuccessLister implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    private UserService userService;

    /**
     * 把RememberMeAuthenticationToken转换成系统自己的user
     * username：对应用户ID
     * password：对应用户手机号码
     *
     * @param event 事件主题
     * */
    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        try {
            Authentication authentication = event.getAuthentication();
            if (authentication instanceof RememberMeAuthenticationToken) {
                UserDetails principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                if (principal.isAccountNonLocked() && principal.isCredentialsNonExpired() && principal.isAccountNonExpired()) {
                    UserPO user = userService.getByAccount(principal.getUsername());
                    User auth = this.generateAuthentication(authentication, user);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private User generateAuthentication(Authentication authentication, UserPO user) {
        User auth = new User(authentication.getAuthorities());
        BeanCopierUtil.copy(user, auth);
        return auth;
    }
}

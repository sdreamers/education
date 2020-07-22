package com.sixing.base.security.provider;

import com.sixing.base.constant.ResponseMsgConstant;
import com.sixing.base.domain.user.UserPO;
import com.sixing.base.security.domain.User;
import com.sixing.base.security.domain.UsernamePasswordCaptchaAuthToken;
import com.sixing.base.security.utils.SecurityConstant;
import com.sixing.base.utils.BeanCopierUtil;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.education.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 检测手机号码和验证码的提供者
 *
 * @author Administrator
 */
@Component
public class CaptchaAuthenticationProvider implements AuthenticationProvider {

    private static final Log log = LogFactory.getLog(WebSecurityConfigurerAdapter.class);

    @Autowired
    private UserService userService;
    /*@Autowired
    private RoleService roleService;*/

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            UsernamePasswordCaptchaAuthToken token = (UsernamePasswordCaptchaAuthToken) authentication;
            String name = (String) token.getPrincipal();
            String password = (String) token.getCredentials();
            UserPO user = userService.getByAccount(name);
            if (user == null) {
                throw new UsernameNotFoundException(ResponseMsgConstant.MerchantNotExistExceptionMsg);
            }
            if (StringUtils.isEmpty(password) || !password.equals(user.getPassword())){
                throw new BadCredentialsException(ResponseMsgConstant.AuthFailedExceptionMsg);
            }
            if ("0".equals(user.getStatus())) {
                throw new DisabledException(ResponseMsgConstant.MerchantIsDisabledExceptionMsg);
            }
            // 检查拥有哪些页面
            Set<String> pages = new HashSet<>();
            List<GrantedAuthority> list = this.addRolePrefix(pages);
            return this.generateAuthentication(user, list);
        } catch (Exception e) {
            log.error(e);
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    private User generateAuthentication(UserPO user, List<GrantedAuthority> list) {
        User token = new User(list);
        BeanCopierUtil.copy(user, token);
        return token;
    }

    private List<GrantedAuthority> addRolePrefix(Set<String> roles) {
        // 权限如果前缀是ROLE_, security就会认为这是个角色信息，而不是权限，
        // 例如ROLE_ADMIN就是ADMIN角色，CAN_SEND就是CAN_SEND权限
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }
        return roles.stream().map(item -> new SimpleGrantedAuthority(SecurityConstant.ROLE_PREFIX + item)).collect(Collectors.toList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordCaptchaAuthToken.class);
    }
}

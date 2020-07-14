package com.sixing.base.security.service;

import com.sixing.base.domain.user.UserPO;
import com.sixing.base.security.utils.SecurityConstant;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;
    /*@Autowired
    private RoleService roleService;*/

    /**
     * 用于RememberMeFilter通过cookie验证合法性
     *
     * @param name 用户名
     * */
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        try {
            UserPO user = userService.getByName(name);
            if (user == null) {
                throw new UsernameNotFoundException("用户名错误, 请检查");
            }
            // 拥有的页面
            Set<String> role = new HashSet<>();
            role.add(SecurityConstant.HAS_AUTHENTICATED_KEY);
            List<GrantedAuthority> list = this.addRolePrefix(role);
            return new User(user.getName(), user.getPassword(), list);
        } catch (ServiceException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    private List<GrantedAuthority> addRolePrefix(Set<String> role) {
        // 权限如果前缀是ROLE_, security就会认为这是个角色信息，而不是权限，
        // 例如ROLE_ADMIN就是ADMIN角色，CAN_SEND就是CAN_SEND权限
        if (CollectionUtils.isEmpty(role)) {
            return new ArrayList<>();
        }
        return role.stream().map(item -> new SimpleGrantedAuthority(SecurityConstant.ROLE_PREFIX + item)).collect(Collectors.toList());
    }
}

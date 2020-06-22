package com.sixing.base.security.metadatasource;

import com.sixing.base.security.utils.NotNeedAuthenticationUrl;
import com.sixing.base.security.utils.SecurityConstant;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 根据请求url判断该url需要的角色
 *
 * @author Administrator
 * */
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /*@Autowired
    private RoleService roleService;
*/
    /**
     * 获取URL需要的角色
     *
     * 注意：该方法如果返回null,则代表url不要任何权限
     * */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        try {
            // 根据请求地址和http method获取该请求需要的角色
            String requestUri = ((FilterInvocation) object).getHttpRequest().getRequestURI();
            if (NotNeedAuthenticationUrl.contain(requestUri)) {
                return null;
            }
            if (requestUri.startsWith(SecurityConstant.KEY_ADMIN_PREFIX)) {
                requestUri = requestUri.substring(SecurityConstant.KEY_ADMIN_PREFIX.length() - 1);
            }
            /*List<RolePO> roles = roleService.listByUrl(requestUri);*/
            List<Object> roles = null;
            if (CollectionUtils.isEmpty(roles)) {
                // 需要登录
                return this.authenticatedAttribute();
            }
            String[] rolesTypes = roles.stream().map(item -> SecurityConstant.ROLE_PREFIX /*+ item.getType()*/).toArray(String[]::new);
            return SecurityConfig.createList(rolesTypes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Collection<ConfigAttribute> authenticatedAttribute() {
        List<String> authenticatedRoles = new ArrayList<>();
        authenticatedRoles.add(SecurityConstant.ROLE_PREFIX + SecurityConstant.HAS_AUTHENTICATED_KEY);
        return SecurityConfig.createList(authenticatedRoles.toArray(new String[0]));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
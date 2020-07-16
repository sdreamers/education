package com.sixing.base.config;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.security.filter.SimpleCrossDomainFilter;
import com.sixing.base.security.filter.UsernamePasswordCaptchaAuthenticationFilter;
import com.sixing.base.security.handler.AccessDeniedHandler;
import com.sixing.base.security.handler.LoginFailureHandler;
import com.sixing.base.security.handler.LoginSuccessHandler;
import com.sixing.base.security.handler.LogoutSuccessHandler;
import com.sixing.base.security.manager.UrlAccessDecisionManager;
import com.sixing.base.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.sixing.base.security.provider.CaptchaAuthenticationProvider;
import com.sixing.base.security.service.UserDetailsServiceImpl;
import com.sixing.base.security.utils.SecurityConstant;
import com.sixing.base.utils.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * spring security配置类
 *
 * @author Administrator
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Log log = LogFactory.getLog(WebSecurityConfigurerAdapter.class);

    private static final String REMEMBER_ME_KEY = "education-remember-me";

    @Autowired
    private UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;
    @Autowired
    private UrlAccessDecisionManager urlAccessDecisionManager;
    @Autowired
    private CaptchaAuthenticationProvider captchaAuthenticationProvider;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private RememberMeServices rememberMeServices;

    @Value("${server.max-session-num}")
    private Integer maxSessionNum;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 禁用匿名账户
        httpSecurity.anonymous().disable();
        // 防止跨站请求伪造
        httpSecurity.csrf().disable();
        // 所有请求都判断权限
        httpSecurity.authorizeRequests().anyRequest().authenticated().withObjectPostProcessor(this.objectPostProcessor());
        // 登录设置
        httpSecurity.addFilter(this.loginFilter()).authenticationProvider(captchaAuthenticationProvider).formLogin().disable();
        // 登出设置
        httpSecurity.logout().logoutUrl("/authentication/logout").logoutSuccessHandler(logoutSuccessHandler);
        // 异常捕捉
        httpSecurity.exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(this.authenticationEntryPoint());
        // 记住我
        httpSecurity
                .rememberMe()
                .rememberMeServices(rememberMeServices)
                .tokenValiditySeconds(30 * 24 * 60 * 60)
                .key(REMEMBER_ME_KEY);
        // session管理
        httpSecurity.sessionManagement().maximumSessions(maxSessionNum).sessionRegistry(this.sessionRegistry());
        // 跨域过滤器
        httpSecurity.cors();
        httpSecurity.addFilterBefore(new SimpleCrossDomainFilter(), ChannelProcessingFilter.class);
    }

    @Bean
    public UsernamePasswordCaptchaAuthenticationFilter loginFilter() throws Exception {
        UsernamePasswordCaptchaAuthenticationFilter loginFilter = new UsernamePasswordCaptchaAuthenticationFilter();
        loginFilter.setAuthenticationManager(this.authenticationManager());
        loginFilter.setRememberMeServices(rememberMeServices);
        loginFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/authentication/login", "POST"));
        loginFilter.setUsernameParameter(SecurityConstant.KEY_USERNAME);
        loginFilter.setPasswordParameter(SecurityConstant.KEY_PASSWORD);
        loginFilter.setCaptchaParameter(SecurityConstant.KEY_CAPTCHA);
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        return loginFilter;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        // 拦截/**/admin、/authentication/**、/user/**请求，其余请求不进入security
        //web.ignoring().regexMatchers("^((?!((/admin.*)|/authentication/.*)).)+$").antMatchers(HttpMethod.OPTIONS);
        super.configure(web);
    }

    /**
     * 资源、权限控制没有可访问的菜单权限，请联系客服
     */
    private ObjectPostProcessor objectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
                o.setAccessDecisionManager(urlAccessDecisionManager);
                return o;
            }
        };
    }

    /**
     * 认证鉴权异常入口
     * <p>
     * TODO  根据异常划分返回内容
     */
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            log.error(e);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            ResultModel<Void> resultModel = new ResultModel<>();
            resultModel.setCode(Constant.RESPONSE_ERROR_CODE);
            if (e instanceof AuthenticationCredentialsNotFoundException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resultModel.setMessage("未登录");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                resultModel.setMessage(e.getMessage());
            }
            out.write(JsonUtils.toJson(resultModel));
            out.flush();
            out.close();
        };
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AbstractRememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsService);
        rememberMeServices.setParameter(SecurityConstant.KEY_REMEMBER_ME);
        return rememberMeServices;
    }
}
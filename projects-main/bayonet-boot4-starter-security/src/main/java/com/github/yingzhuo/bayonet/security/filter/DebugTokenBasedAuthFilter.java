package com.github.yingzhuo.bayonet.security.filter;

import com.github.yingzhuo.bayonet.security.authentication.UserDetailsAuth;
import com.github.yingzhuo.bayonet.security.event.AuthenticationSuccessEvent;
import com.github.yingzhuo.bayonet.security.memory.InMemoryUserDetailsService;
import com.github.yingzhuo.bayonet.security.token.BearerHeaderTokenResolver;
import com.github.yingzhuo.bayonet.security.token.TokenResolver;
import com.github.yingzhuo.bayonet.utility.PropertiesUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

import static com.github.yingzhuo.bayonet.security.filter.AuthFilterHelper.ATTRIBUTE_AUTHENTICATION_NAME;

/**
 * 调试用的 Token 认证过滤器。
 * <p><strong>警告：仅用于开发/调试环境，禁止在生产环境使用。</strong></p>
 * <p>此过滤器将请求中解析到的 Token 直接作为用户名进行认证，不验证密码。
 * 任何知道用户名的人都可以通过此过滤器认证成功，存在严重安全风险。</p>
 *
 * <p>用户信息通过 {@link Properties} 文件配置：
 * <pre>{@code
 * admin={noop}admin123,ROLE_ADMIN,ROLE_USER
 * user=pass456,ROLE_USER,enabled
 * guest=guest,ROLE_GUEST,disabled
 * }</pre>
 * </p>
 *
 * @author 应卓
 * @see TokenBasedAuthFilter
 * @since 4.1.1
 */
@Slf4j
@Setter
public class DebugTokenBasedAuthFilter extends OncePerRequestFilter implements ApplicationEventPublisherAware {

    private final UserDetailsService userDetailsService;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private TokenResolver tokenResolver = new BearerHeaderTokenResolver();
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 构造器
     *
     * @param propertiesLocation Properties 文件路径，支持 classpath:/、file:/ 等 Spring 资源路径
     */
    public DebugTokenBasedAuthFilter(String propertiesLocation) {
        this(propertiesLocation, false);
    }

    /**
     * 构造器
     *
     * @param propertiesLocation Properties 文件路径，支持 classpath:/、file:/ 等 Spring 资源路径
     * @param xmlFormat          是否为 XML 格式
     */
    public DebugTokenBasedAuthFilter(String propertiesLocation, boolean xmlFormat) {
        this(PropertiesUtils.loadProperties(propertiesLocation, xmlFormat));
    }

    /**
     * 构造器
     *
     * @param usersProperties 用户属性配置
     */
    public DebugTokenBasedAuthFilter(Properties usersProperties) {
        Assert.notEmpty(usersProperties, "usersProperties cannot be empty");
        this.userDetailsService = new InMemoryUserDetailsService(usersProperties);
    }

    /**
     * 构造器
     *
     * @param users 用户
     */
    public DebugTokenBasedAuthFilter(UserDetails... users) {
        Assert.notEmpty(users, "users cannot be empty");
        this.userDetailsService =
                new InMemoryUserDetailsService(
                        Arrays.stream(users)
                                .filter(Objects::nonNull)
                                .toList()
                );
    }

    /**
     * 构造器
     *
     * @param users 用户
     */
    public DebugTokenBasedAuthFilter(Collection<UserDetails> users) {
        Assert.notEmpty(users, "users cannot be empty");
        this.userDetailsService =
                new InMemoryUserDetailsService(
                        users.stream()
                                .filter(Objects::nonNull)
                                .toList()
                );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (AuthFilterHelper.authenticationIsNotRequired(securityContextHolderStrategy)) {
            filterChain.doFilter(request, response);
            return;
        }

        var currentWebRequest = new ServletWebRequest(request);

        // 解析 token, token 即用户名
        var username = tokenResolver.resolve(new ServletWebRequest(request, response));
        if (!StringUtils.hasText(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 认证（不验证密码）
        try {
            var userDetails = this.userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                var auth = new UserDetailsAuth(userDetails);
                securityContextHolderStrategy.getContext().setAuthentication(auth);
                request.setAttribute(ATTRIBUTE_AUTHENTICATION_NAME, auth);

                if (applicationEventPublisher != null) {
                    applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(currentWebRequest, username, auth));
                }
            }
        } catch (UsernameNotFoundException ignored) {
            // noop
        } catch (AuthenticationException e) {
            securityContextHolderStrategy.clearContext();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}

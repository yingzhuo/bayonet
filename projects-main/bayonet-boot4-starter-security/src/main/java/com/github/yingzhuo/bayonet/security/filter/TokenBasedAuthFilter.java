package com.github.yingzhuo.bayonet.security.filter;

import com.github.yingzhuo.bayonet.security.authentication.AuthenticationDetailsCreator;
import com.github.yingzhuo.bayonet.security.authentication.UserDetailsAuth;
import com.github.yingzhuo.bayonet.security.event.AuthenticationFailureEvent;
import com.github.yingzhuo.bayonet.security.event.AuthenticationSuccessEvent;
import com.github.yingzhuo.bayonet.security.event.TokenResolvedEvent;
import com.github.yingzhuo.bayonet.security.token.BearerHeaderTokenResolver;
import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import com.github.yingzhuo.bayonet.security.token.TokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.util.Assert;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.github.yingzhuo.bayonet.security.filter.AuthFilterHelper.ATTRIBUTE_AUTHENTICATION_NAME;
import static com.github.yingzhuo.bayonet.security.filter.AuthFilterHelper.ATTRIBUTE_TOKEN_NAME;

/**
 * 基于 Token 的认证过滤器。
 * <p>从请求中提取 Token → 通过 {@link TokenConverter} 转换为 {@link UserDetails}
 * → 包装为 {@link UserDetailsAuth} 并设置 SecurityContext。
 * 可配合 {@link TokenResolver} 和 {@link TokenConverter} 灵活配置。</p>
 *
 * <p>此外还支持：</p>
 * <ul>
 *   <li>{@link org.springframework.security.web.authentication.RememberMeServices RememberMeServices} — 记住我功能</li>
 *   <li>{@link org.springframework.context.ApplicationEventPublisher ApplicationEventPublisher} — 发布认证生命周期事件</li>
 * </ul>
 *
 * <pre>{@code
 * var filter = new TokenBasedAuthFilter();
 * filter.setTokenResolver(new BearerHeaderTokenResolver());
 * filter.setTokenConverter(new JwtTokenConverter());
 * filter.setAuthenticationEntryPoint(new Http403ForbiddenEntryPoint());
 * filter.setRememberMeServices(new PersistentTokenBasedRememberMeServices(...));
 * filter.setApplicationEventPublisher(applicationEventPublisher);
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Setter
public class TokenBasedAuthFilter extends OncePerRequestFilter implements ApplicationEventPublisherAware {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private TokenResolver tokenResolver = new BearerHeaderTokenResolver();
    private TokenConverter tokenConverter;
    private @Nullable RememberMeServices rememberMeServices;
    private @Nullable AuthenticationDetailsCreator authenticationDetailsCreator;
    private @Nullable AuthenticationEntryPoint authenticationEntryPoint;
    private @Nullable ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected final void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy is required");
        Assert.notNull(tokenResolver, "tokenResolver is required");
        Assert.notNull(tokenConverter, "tokenConverter is required");

        if (AuthFilterHelper.authenticationIsNotRequired(securityContextHolderStrategy)) {
            log.debug("authentication NOT required. skipping...");
            filterChain.doFilter(request, response);
            return;
        }

        var currentWebRequest = new ServletWebRequest(request, response);

        // 解析 token
        var token = tokenResolver.resolve(currentWebRequest);
        if (token == null) {
            log.debug("token cannot be resolved. skipping...");
            filterChain.doFilter(request, response);
            return;
        }

        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new TokenResolvedEvent(currentWebRequest, token));
        }

        // 认证
        try {
            var userDetails = tokenConverter.convert(token);

            if (userDetails == null) {
                log.debug("could not convert token to UserDetails: token={}", token);
                log.debug("skipping...");
                filterChain.doFilter(request, response);
                return;
            }

            if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked() || !userDetails.isAccountNonExpired() || !userDetails.isCredentialsNonExpired()) {
                throw new DisabledException("user is disabled");
            }

            var auth = new UserDetailsAuth(userDetails);
            if (authenticationDetailsCreator != null) {
                auth.setDetails(authenticationDetailsCreator.create(currentWebRequest)); // 详情
            }

            securityContextHolderStrategy.getContext().setAuthentication(auth);

            onAuthenticationSuccess(auth, currentWebRequest);

            if (rememberMeServices != null) {
                rememberMeServices.loginSuccess(request, response, auth);
            }

            request.setAttribute(ATTRIBUTE_TOKEN_NAME, token); // 偷偷放在request里
            request.setAttribute(ATTRIBUTE_AUTHENTICATION_NAME, auth); // 偷偷放在request里

            if (applicationEventPublisher != null) {
                applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(currentWebRequest, token, auth));
            }

        } catch (AuthenticationException e) {
            request.removeAttribute(ATTRIBUTE_AUTHENTICATION_NAME);
            request.removeAttribute(ATTRIBUTE_TOKEN_NAME);

            securityContextHolderStrategy.clearContext();

            if (rememberMeServices != null) {
                rememberMeServices.loginFail(request, response);
            }

            if (applicationEventPublisher != null) {
                applicationEventPublisher.publishEvent(new AuthenticationFailureEvent(currentWebRequest, token, e));
            }

            if (authenticationEntryPoint != null) {
                authenticationEntryPoint.commence(request, response, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 认证成功后的回调。
     * <p>子类可覆盖此方法以记录审计日志、更新最后登录时间等。</p>
     *
     * @param auth           Authentication 实例
     * @param currentRequest 当前 Web 请求
     * @throws AuthenticationException 回调中可抛出异常中断认证
     */
    protected void onAuthenticationSuccess(Authentication auth, WebRequest currentRequest) throws AuthenticationException {
        // noop
    }

}

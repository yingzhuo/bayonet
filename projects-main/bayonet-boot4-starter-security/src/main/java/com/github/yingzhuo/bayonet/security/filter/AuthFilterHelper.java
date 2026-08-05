package com.github.yingzhuo.bayonet.security.filter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import static java.util.Objects.requireNonNullElseGet;

/**
 * 认证过滤器实用工具。
 * <p>提供认证过滤器通用的 request 属性名常量与"是否已认证"的判断。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthFilterHelper {

    /**
     * 存放 Token 的 request 属性名。
     */
    public static final String ATTRIBUTE_TOKEN_NAME = AuthFilterHelper.class.getName() + "#token";

    /**
     * 存放 {@link org.springframework.security.core.Authentication Authentication} 的 request 属性名。
     */
    public static final String ATTRIBUTE_AUTHENTICATION_NAME = AuthFilterHelper.class.getName() + "#authentication";

    // ------

    /**
     * 判断当前是否已认证（无需再次认证），使用默认的 {@link SecurityContextHolderStrategy}。
     *
     * @return {@code true} 表示已认证且非匿名，无需再次认证
     */
    public static boolean authenticationIsNotRequired() {
        return authenticationIsNotRequired(null);
    }

    /**
     * 判断当前是否已认证（无需再次认证）。
     * <p>已认证且非 {@link AnonymousAuthenticationToken} 时返回 {@code true}。</p>
     *
     * @param strategy 安全上下文持有策略，为 {@code null} 时使用默认策略
     * @return {@code true} 表示已认证且非匿名，无需再次认证
     */
    public static boolean authenticationIsNotRequired(@Nullable SecurityContextHolderStrategy strategy) {
        strategy = requireNonNullElseGet(strategy, SecurityContextHolder::getContextHolderStrategy);

        var existingAuth = strategy.getContext().getAuthentication();
        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return false;
        }
        return (!(existingAuth instanceof AnonymousAuthenticationToken));
    }

}

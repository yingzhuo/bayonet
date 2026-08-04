package com.github.yingzhuo.bayonet.security.filter;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import java.util.Objects;

/**
 * 认证过滤器实用工具
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthFilterHelper {

    public static final String ATTRIBUTE_TOKEN_NAME = AuthFilterHelper.class.getName() + "#token";
    public static final String ATTRIBUTE_AUTHENTICATION_NAME = AuthFilterHelper.class.getName() + "#authentication";

    public static boolean authenticationIsNotRequired() {
        return authenticationIsNotRequired(null);
    }

    public static boolean authenticationIsNotRequired(@Nullable SecurityContextHolderStrategy strategy) {
        strategy = Objects.requireNonNullElseGet(strategy, SecurityContextHolder::getContextHolderStrategy);

        var existingAuth = strategy.getContext().getAuthentication();
        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return false;
        }
        return (!(existingAuth instanceof AnonymousAuthenticationToken));
    }

}

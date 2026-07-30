package com.github.yingzhuo.bayonet.security.filter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

/**
 * 内部工具
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticationFilterUtils {

    public static boolean authenticationIsRequired(SecurityContextHolderStrategy strategy) {
        var existingAuth = strategy.getContext().getAuthentication();
        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }
        return (existingAuth instanceof AnonymousAuthenticationToken);
    }

}

package com.github.yingzhuo.bayonet.security.filter;

import com.github.yingzhuo.bayonet.security.authentication.RichUserDetails;
import com.github.yingzhuo.bayonet.security.memory.InMemoryUserDetailsService;
import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import com.github.yingzhuo.bayonet.utility.collection.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

/**
 * 调试用的 Token 认证过滤器。
 * <p><strong>警告：仅用于开发/调试环境，禁止在生产环境使用。</strong></p>
 * <p>该过滤器将请求中的 Token 直接视为用户名，从内存用户列表中加载用户完成认证，
 * 不执行真实的 Token 校验。</p>
 * <p>构造参数接受任意 {@link UserDetails}，<b>推荐使用 {@link RichUserDetails}</b>
 * 以获得更丰富的用户信息。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@Slf4j
public class DebugTokenBasedAuthFilter extends TokenBasedAuthFilter {

    /**
     * 构造器
     *
     * @param users 内存用户列表（非空），推荐使用 {@link RichUserDetails}
     */
    public DebugTokenBasedAuthFilter(UserDetails... users) {
        if (ArrayUtils.isEmpty(users)) {
            throw new IllegalArgumentException("users must not be empty");
        }
        super.setTokenConverter(new DebugTokenConverter(new InMemoryUserDetailsService(users)));
    }

    /**
     * 构造器
     *
     * @param users 内存用户集合（非空），推荐使用 {@link RichUserDetails}
     */
    public DebugTokenBasedAuthFilter(Collection<UserDetails> users) {
        this(users.toArray(new UserDetails[0]));
    }

    @Override
    public void setTokenConverter(TokenConverter tokenConverter) {
        throw new UnsupportedOperationException();
    }

    // ------

    private record DebugTokenConverter(UserDetailsService service) implements TokenConverter {
        public @Nullable UserDetails convert(String username) throws AuthenticationException {
            try {
                return service.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                return null;
            }
        }
    }
}

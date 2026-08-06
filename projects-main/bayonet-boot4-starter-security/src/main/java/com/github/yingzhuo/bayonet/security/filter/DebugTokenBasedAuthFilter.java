package com.github.yingzhuo.bayonet.security.filter;

import com.github.yingzhuo.bayonet.security.authentication.RichUserDetails;
import com.github.yingzhuo.bayonet.security.authentication.RichUserDetailsConverter;
import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import com.github.yingzhuo.bayonet.utility.collection.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        super.setTokenConverter(new DebugTokenConverter(new InMemoryUserDetailsManager(users)));
    }

    /**
     * 构造器
     *
     * @param users 内存用户集合（非空），推荐使用 {@link RichUserDetails}
     */
    public DebugTokenBasedAuthFilter(Collection<UserDetails> users) {
        this(users.toArray(new UserDetails[0]));
    }

    /**
     * 构造器（从配置文件加载用户）。
     * <p>读取配置文件内容，按行解析为 {@link RichUserDetails} 并构建内存用户列表。
     * 每行格式如下（第一个为 id、第二个为 username、中间为权限、
     * 最后一个若为 {@code true}/{@code false}/{@code yes}/{@code no} 则作为是否启用，否则视为权限）：</p>
     *
     * <pre>{@code
     * id,username,authority1,authority2,...,enabled
     * }</pre>
     *
     * <p>空白行会被忽略。</p>
     *
     * <pre>{@code
     * 0001,admin,ROLE_ADMIN,ROLE_USER,true
     * 0002,yingzhuo,ROLE_USER
     * }</pre>
     *
     * @param configResource 配置文件资源（非 {@code null}）
     * @throws UncheckedIOException 读取配置文件失败
     */
    public DebugTokenBasedAuthFilter(Resource configResource) {
        Assert.notNull(configResource, "config resource must not be null");

        try {
            var converter = new RichUserDetailsConverter();
            var users = configResource.getContentAsString(UTF_8)
                    .lines()
                    .filter(StringUtils::hasText)
                    .map(converter::convert)
                    .toArray(UserDetails[]::new);
            super.setTokenConverter(new DebugTokenConverter(new InMemoryUserDetailsManager(users)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setTokenConverter(TokenConverter tokenConverter) {
        throw new UnsupportedOperationException("DebugTokenBasedAuthFilter does not support token converter setting");
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

package com.github.yingzhuo.bayonet.security.token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.WebRequest;

/**
 * {@link TokenResolver} 的空实现。
 * <p>始终返回 {@code null}，表示请求中无有效 Token。
 * 适用于默认兜底、测试场景或禁用 Token 认证的场景。</p>
 *
 * <pre>{@code
 * var resolver = NullTokenResolver.INSTANCE;
 * String token = resolver.resolve(webRequest); // 始终返回 null
 * }</pre>
 *
 * @see TokenResolver
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NullTokenResolver implements TokenResolver, Ordered {

    /**
     * 单例实例。
     */
    public static final NullTokenResolver INSTANCE = new NullTokenResolver();

    /**
     * 始终返回 {@code null}。
     *
     * @param webRequest 当前 Web 请求（忽略）
     * @return 始终为 {@code null}
     */
    @Override
    public @Nullable String resolve(WebRequest webRequest) {
        return null;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}

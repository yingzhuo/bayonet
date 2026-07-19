package com.github.yingzhuo.bayonet.security.token;

import org.jspecify.annotations.Nullable;
import org.springframework.web.context.request.WebRequest;

/**
 * HTTP 请求中 Token 提取器。
 * <p>从 {@link WebRequest} 中解析并返回 Token 字符串，供 {@link TokenConverter} 使用。</p>
 *
 * <pre>{@code
 * var resolver = new BearerHeaderTokenResolver();
 * String token = resolver.resolve(webRequest);
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public interface TokenResolver {

    /**
     * 从请求中提取 Token 字符串。
     *
     * @param webRequest 当前 Web 请求
     * @return Token 字符串，请求中无有效 Token 时返回 {@code null}
     */
    @Nullable String resolve(WebRequest webRequest);

}

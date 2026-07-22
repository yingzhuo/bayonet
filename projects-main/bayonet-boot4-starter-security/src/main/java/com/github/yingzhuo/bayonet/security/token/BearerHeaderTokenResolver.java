package com.github.yingzhuo.bayonet.security.token;

import org.springframework.http.HttpHeaders;

/**
 * 从 {@code Authorization: Bearer <token>} 请求头中提取 Token 的解析器。
 * <p>继承自 {@link HttpHeaderTokenResolver}，固定使用 {@link HttpHeaders#AUTHORIZATION}
 * 作为 header 名称，{@code "Bearer "} 作为前缀。</p>
 *
 * <pre>{@code
 * var resolver = new BearerHeaderTokenResolver();
 * String token = resolver.resolve(webRequest);
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class BearerHeaderTokenResolver extends HttpHeaderTokenResolver {

    public BearerHeaderTokenResolver() {
        this(0);
    }

    public BearerHeaderTokenResolver(int order) {
        super(HttpHeaders.AUTHORIZATION, "Bearer ", order);
    }

}

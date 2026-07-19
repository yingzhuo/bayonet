package com.github.yingzhuo.bayonet.security.token;

import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

/**
 * 从 HTTP 请求头中提取 Token 的解析器。
 * <p>支持指定 header 名称和可选前缀。前缀匹配成功后，返回去除前缀和首尾空格的 token 值。</p>
 *
 * <pre>{@code
 * // 从 X-Auth-Token 头提取
 * var resolver = new HttpHeaderTokenResolver("X-Auth-Token");
 * String token = resolver.resolve(webRequest);
 *
 * // 从 Authorization: Bearer xxx 提取
 * var resolver = new HttpHeaderTokenResolver(HttpHeaders.AUTHORIZATION, "Bearer ");
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class HttpHeaderTokenResolver implements TokenResolver, Ordered {

    private final String headerName;
    private final String prefix;
    private final int order;

    /**
     * 构造器。
     *
     * @param headerName HTTP 请求头名称（非空）
     * @throws IllegalArgumentException 若 {@code headerName} 为空
     */
    public HttpHeaderTokenResolver(String headerName) {
        this(headerName, "", 0);
    }

    /**
     * 构造器（无前缀，指定排序）。
     *
     * @param headerName HTTP 请求头名称（非空）
     * @param order      排序值（越小优先级越高）
     * @throws IllegalArgumentException 若 {@code headerName} 为空
     */
    public HttpHeaderTokenResolver(String headerName, int order) {
        this(headerName, "", order);
    }

    /**
     * 构造器（指定前缀和排序）。
     *
     * @param headerName HTTP 请求头名称（非空）
     * @param prefix     token 值前缀（如 {@code "Bearer "}），为 {@code null} 时视为空字符串
     * @param order      排序值（越小优先级越高）
     * @throws IllegalArgumentException 若 {@code headerName} 为空
     */
    public HttpHeaderTokenResolver(String headerName, @Nullable String prefix, int order) {
        Assert.hasText(headerName, "header name must not be empty");
        this.headerName = headerName;
        this.prefix = Objects.requireNonNullElse(prefix, "");
        this.order = order;
    }

    @Override
    public @Nullable String resolve(WebRequest webRequest) {
        var headerValue = webRequest.getHeader(this.headerName);
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }

        if (headerValue.startsWith(this.prefix)) {
            return headerValue.substring(this.prefix.length()).trim();
        }

        return null;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

}

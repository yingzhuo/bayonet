package com.github.yingzhuo.bayonet.webcli.util;

import com.github.yingzhuo.bayonet.webcli.interceptor.BearerAuthClientHttpRequestInterceptor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * {@link ClientHttpRequestInterceptor} 的工厂工具类。
 *
 * <p>提供便捷方法快速创建常用的请求拦截器，包括 Basic 认证和 Bearer Token 认证。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * // Basic 认证（UTF-8）
 * var interceptor = InterceptorFactories.createBasicAuthInterceptor("user", "pass");
 *
 * // Basic 认证（自定义编码）
 * var interceptor = InterceptorFactories.createBasicAuthInterceptor("user", "pass", StandardCharsets.ISO_8859_1);
 *
 * // Bearer Token 认证
 * var interceptor = InterceptorFactories.createBearerAuthInterceptor("my-token");
 * }</pre>
 *
 * @author 应卓
 * @see ClientHttpRequestInterceptor
 * @see BasicAuthenticationInterceptor
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InterceptorFactories {

    /**
     * 创建 Basic 认证拦截器（使用 UTF-8 编码）。
     * <p>使用 {@link StandardCharsets#UTF_8 UTF-8} 编码对用户名和密码进行编码。
     * 相当于调用 {@link #createBasicAuthInterceptor(String, String, Charset) createBasicAuthInterceptor(username, password, StandardCharsets.UTF_8)}。</p>
     *
     * @param username 用户名
     * @param password 密码
     * @return BasicAuthenticationInterceptor 实例
     * @throws IllegalArgumentException username 或 password 为空时抛出
     */
    public static ClientHttpRequestInterceptor createBasicAuthInterceptor(String username, String password) {
        return createBasicAuthInterceptor(username, password, StandardCharsets.UTF_8);
    }

    /**
     * 创建 Basic 认证拦截器（支持自定义编码）。
     *
     * @param username 用户名
     * @param password 密码
     * @param charset  字符编码，为 {@code null} 时使用 {@link BasicAuthenticationInterceptor} 内部默认值
     * @return BasicAuthenticationInterceptor 实例
     * @throws IllegalArgumentException username 或 password 为空时抛出
     */
    public static ClientHttpRequestInterceptor createBasicAuthInterceptor(
            String username, String password, @Nullable Charset charset) {
        Assert.hasText(username, "username must not be empty");
        Assert.hasText(password, "password must not be empty");
        return new BasicAuthenticationInterceptor(username, password, charset);
    }

    /**
     * 创建 Bearer Token 认证拦截器。
     * <p>在请求头中添加 {@code Authorization: Bearer &lt;token&gt;}。
     * 若请求已包含 {@code Authorization} 头，则不会覆盖已有值。</p>
     *
     * @param token Bearer Token
     * @return ClientHttpRequestInterceptor 实例
     * @throws IllegalArgumentException token 为空时抛出
     */
    public static ClientHttpRequestInterceptor createBearerAuthInterceptor(String token) {
        Assert.hasText(token, "token must not be empty");
        return new BearerAuthClientHttpRequestInterceptor(token);
    }

}

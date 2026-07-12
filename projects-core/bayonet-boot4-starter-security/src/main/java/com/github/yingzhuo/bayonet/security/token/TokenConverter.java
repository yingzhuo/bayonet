package com.github.yingzhuo.bayonet.security.token;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Token 字符串 → {@link Authentication} 转换器。
 * <p>将 {@link TokenResolver} 提取的 Token 字符串转换为 Spring Security
 * {@link Authentication} 对象，供 {@link com.github.yingzhuo.bayonet.security.filter.AbstractTokenBasedAuthenticationFilter} 使用。</p>
 *
 * <pre>{@code
 * public class JwtTokenConverter implements TokenConverter<JwtAuthentication> {
 *     public @Nullable JwtAuthentication convert(String token) {
 *         // 解析 JWT → 返回 Authentication
 *     }
 * }
 * }</pre>
 *
 * @param <A> Authentication 类型
 */
public interface TokenConverter<A extends Authentication> {

    /**
     * 将 Token 字符串转换为 {@link Authentication}。
     *
     * @param token Token 字符串
     * @return Authentication 实例，返回 {@code null} 表示跳过认证
     * @throws AuthenticationException 认证失败
     */
    @Nullable A convert(String token) throws AuthenticationException;

}

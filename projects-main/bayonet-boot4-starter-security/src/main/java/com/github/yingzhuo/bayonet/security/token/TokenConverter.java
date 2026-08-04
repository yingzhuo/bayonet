package com.github.yingzhuo.bayonet.security.token;

import com.github.yingzhuo.bayonet.security.filter.TokenBasedAuthFilter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Token 字符串 → {@link UserDetails} 转换器。
 * <p>将 {@link TokenResolver} 提取的 Token 字符串转换为 Spring Security
 * {@link UserDetails} 对象，供 {@link TokenBasedAuthFilter} 使用。</p>
 *
 * <pre>{@code
 * public class JwtTokenConverter implements TokenConverter {
 *     public @Nullable UserDetails convert(String token) {
 *         // 解析 JWT → 返回 UserDetails
 *     }
 * }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public interface TokenConverter {

    /**
     * 将 Token 字符串转换为 {@link UserDetails}。
     *
     * @param token Token 字符串
     * @return {@link UserDetails} 实例，返回 {@code null} 表示跳过认证
     * @throws AuthenticationException 认证失败
     */
    @Nullable UserDetails convert(String token) throws AuthenticationException;

}

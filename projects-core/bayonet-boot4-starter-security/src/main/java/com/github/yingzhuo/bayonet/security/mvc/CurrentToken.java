package com.github.yingzhuo.bayonet.security.mvc;

import java.lang.annotation.*;

/**
 * 标记控制器方法参数以注入当前请求的认证 Token。
 *
 * <p>与 {@link CurrentTokenHandlerMethodArgumentResolver} 配合使用，
 * 从 {@link com.github.yingzhuo.bayonet.security.filter.TokenBasedAuthenticationFilter TokenBasedAuthenticationFilter}
 * 存储在 request attribute 中的 token 取值，支持 {@link String} 和 {@link java.util.Optional Optional} 两种类型。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * @GetMapping("/me")
 * public String me(@CurrentToken String token) {
 *     return token;
 * }
 *
 * @GetMapping("/optional")
 * public String me(@CurrentToken Optional<String> tokenOpt) {
 *     return tokenOpt.orElse("anonymous");
 * }
 * }</pre>
 *
 * @author 应卓
 * @see CurrentTokenHandlerMethodArgumentResolver
 * @see com.github.yingzhuo.bayonet.security.filter.TokenBasedAuthenticationFilter
 * @since 4.1.1
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentToken {
}

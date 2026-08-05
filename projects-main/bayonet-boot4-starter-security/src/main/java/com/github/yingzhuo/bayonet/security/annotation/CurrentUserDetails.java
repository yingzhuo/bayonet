package com.github.yingzhuo.bayonet.security.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.annotation.*;

/**
 * 获取当前认证用户的 {@link UserDetails}。
 * <p>组合 Spring Security 的 {@link CurrentSecurityContext}，通过 SpEL 解析当前认证的
 * {@link com.github.yingzhuo.bayonet.security.authentication.UserDetailsAuth UserDetailsAuth}
 * 中持有的 {@link UserDetails} 并注入方法参数。</p>
 *
 * <p><b>注意：</b>该注解依赖认证产生的 {@link Authentication} 为
 * {@link com.github.yingzhuo.bayonet.security.authentication.UserDetailsAuth UserDetailsAuth}
 * （如本库的 {@code TokenBasedAuthFilter}、{@code DebugTokenBasedAuthFilter}），
 * 其他 {@link Authentication} 实现可能导致 SpEL 解析失败。</p>
 *
 * <pre>{@code
 * @GetMapping("/me")
 * public UserDetails me(@CurrentUserDetails UserDetails userDetails) {
 *     return userDetails;
 * }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CurrentSecurityContext(expression = "#root.authentication.user")
public @interface CurrentUserDetails {

    /**
     * 类型不匹配时是否抛出异常。
     *
     * @return {@code true} 表示类型不匹配时抛出异常，默认 {@code false}
     */
    @AliasFor(annotation = CurrentSecurityContext.class, attribute = "errorOnInvalidType")
    boolean errorOnInvalidType() default false;

}

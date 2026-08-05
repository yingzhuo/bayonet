package com.github.yingzhuo.bayonet.security.annotation;

import com.github.yingzhuo.bayonet.security.authentication.RichUserDetails;
import com.github.yingzhuo.bayonet.security.authentication.UserDetailsAuth;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.annotation.*;

/**
 * 获取当前认证用户的出生日期。
 * <p>组合 Spring Security 的 {@link CurrentSecurityContext}，通过 SpEL
 * {@code #root.authentication.user.dob} 解析当前认证用户的出生日期并注入方法参数。</p>
 *
 * <p><b>注意：</b>该注解仅在以下两个前提同时满足时 SpEL 才能正确求值：</p>
 * <ul>
 *   <li>认证产生的 {@link Authentication} 为本库的 {@link UserDetailsAuth}
 *   （其 {@code user} 属性承载当前用户）</li>
 *   <li>{@code user} 的实际类型为 {@link RichUserDetails}（提供 {@code getDob()}）</li>
 * </ul>
 * <p>其他组合（如标准 {@code UsernamePasswordAuthenticationToken} 或普通 {@link UserDetails}）
 * 可能导致 SpEL 解析失败。</p>
 *
 * <pre>{@code
 * @GetMapping("/me")
 * public LocalDate dob(@CurrentUserDob LocalDate dob) {
 *     return dob;
 * }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CurrentSecurityContext(expression = "#root.authentication.user.dob")
public @interface CurrentUserDob {

    /**
     * 类型不匹配时是否抛出异常。
     *
     * @return {@code true} 表示类型不匹配时抛出异常，默认 {@code false}
     */
    @AliasFor(annotation = CurrentSecurityContext.class, attribute = "errorOnInvalidType")
    boolean errorOnInvalidType() default false;

}

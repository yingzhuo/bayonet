package com.github.yingzhuo.bayonet.security.configurer;

import com.github.yingzhuo.bayonet.security.filter.DebugTokenBasedAuthFilter;
import jakarta.servlet.Filter;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.lang.annotation.*;

/**
 * 启用调试认证过滤器的组合注解。
 * <p>组合 {@link AdditionalSecurityFilter @AdditionalSecurityFilter}，
 * 将 {@link com.github.yingzhuo.bayonet.security.filter.DebugTokenBasedAuthFilter DebugTokenBasedAuthFilter}
 * 添加到 Spring Security 过滤器链的
 * {@link com.github.yingzhuo.bayonet.security.configurer.FilterPositionHint#BEFORE BEFORE}
 * 位置。</p>
 *
 * <p><strong>警告：仅用于开发/调试环境，禁止在生产环境使用。</strong></p>
 *
 * <pre>{@code
 * @Configuration
 * @AdditionalDebugAuthFilter
 * public class DebugConfig { }
 * }</pre>
 *
 * @author 应卓
 * @see com.github.yingzhuo.bayonet.security.filter.DebugTokenBasedAuthFilter
 * @see AdditionalSecurityFilter
 * @since 4.1.1
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AdditionalSecurityFilter(value = DebugTokenBasedAuthFilter.class)
public @interface AdditionalDebugAuthFilter {

    /**
     * 定位参考的过滤器类型。
     * <p>{@link AdditionalSecurityFilter#positionFilterType()} 的别名。</p>
     *
     * @return 定位参考的过滤器类型
     */
    @AliasFor(annotation = AdditionalSecurityFilter.class, attribute = "positionFilterType")
    Class<? extends Filter> positionFilterType() default BasicAuthenticationFilter.class;

    /**
     * 定位提示。
     * <p>{@link AdditionalSecurityFilter#hint()} 的别名。</p>
     *
     * @return 定位提示
     * @see FilterPositionHint
     */
    @AliasFor(annotation = AdditionalSecurityFilter.class, attribute = "hint")
    FilterPositionHint hint() default FilterPositionHint.BEFORE;

    /**
     * 跳过此过滤器的 Profile 条件。
     * <p>{@link AdditionalSecurityFilter#skipIfAnyProfileActivated()} 的别名。</p>
     *
     * @return Profile 名称数组
     */
    @AliasFor(annotation = AdditionalSecurityFilter.class, attribute = "skipIfAnyProfileActivated")
    String[] skipIfAnyProfileActivated() default {};

}

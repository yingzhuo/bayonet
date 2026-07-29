package com.github.yingzhuo.bayonet.security.configurer;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.lang.annotation.*;

/**
 * 声明附加 {@link Filter} 到 Spring Security 过滤器链的注解
 *
 * <pre>{@code
 * @Configuration
 * @AdditionalSecurityFilter(filterType = MyFilter.class, hint = FilterPositionHint.AFTER)
 * public class MyConfig { }
 * }</pre>
 *
 * @author 应卓
 * @see AdditionalFilterConfig
 * @see AdditionalSecurityFilterBeanRegistrar
 * @see FilterPositionHint
 * @since 4.1.1
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AdditionalSecurityFilterBeanRegistrar.class)
@Repeatable(AdditionalSecurityFilter.List.class)
public @interface AdditionalSecurityFilter {

    /**
     * 要添加的过滤器类型。
     * <p>{@link #value()} 的别名。</p>
     *
     * @return 过滤器类型
     */
    @AliasFor("value")
    Class<? extends Filter> filterType() default Filter.class;

    /**
     * 要添加的过滤器类型。
     * <p>{@link #filterType} 的别名。</p>
     *
     * @return 过滤器类型
     */
    @AliasFor("filterType")
    Class<? extends Filter> value() default Filter.class;

    /**
     * 定位参考的过滤器类型。
     * <p>相对于此过滤器确定 {@link #hint()} 所指示的位置。</p>
     *
     * @return 定位参考的过滤器类型
     */
    Class<? extends Filter> positionFilterType() default BasicAuthenticationFilter.class;

    /**
     * 定位提示。
     *
     * @return 定位提示
     * @see FilterPositionHint
     */
    FilterPositionHint hint() default FilterPositionHint.AFTER;

    // ------

    /**
     * 可重复注解的容器。
     *
     * @see AdditionalSecurityFilter
     */
    @Inherited
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Import(AdditionalSecurityFilterBeanRegistrar.class)
    @interface List {

        /**
         * 重复的 {@link AdditionalSecurityFilter} 注解数组。
         *
         * @return 注解数组
         */
        AdditionalSecurityFilter[] value();
    }
}

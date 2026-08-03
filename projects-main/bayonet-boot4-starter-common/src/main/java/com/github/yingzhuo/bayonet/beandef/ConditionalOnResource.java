package com.github.yingzhuo.bayonet.beandef;

import com.github.yingzhuo.bayonet.common.Logic;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 条件注解：仅当指定资源（全部或任一）存在时，才注册配置类或 Bean。
 * <p>通过 {@link #logic()} 控制多个资源的组合方式：
 * {@link Logic#AND}（默认）要求全部存在才匹配，{@link Logic#OR} 要求任一存在即匹配。
 * 资源路径支持占位符解析（如 {@code ${custom.location}}）。</p>
 *
 * <pre>{@code
 * @ConditionalOnResource(resources = "classpath:foo.txt")
 * @Configuration
 * public class FooConfiguration { }
 *
 * @ConditionalOnResource(resources = {"classpath:a.txt", "file:/etc/b.conf"}, logic = Logic.OR)
 * @Bean
 * public Bar bar() { }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.1
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnResourceCondition.class)
public @interface ConditionalOnResource {

    /**
     * 资源位置列表。
     *
     * @return 资源位置
     */
    String[] resources() default {};

    /**
     * 资源组合逻辑。
     *
     * @return 组合逻辑，默认 {@link Logic#AND}
     */
    Logic logic() default Logic.AND;

}

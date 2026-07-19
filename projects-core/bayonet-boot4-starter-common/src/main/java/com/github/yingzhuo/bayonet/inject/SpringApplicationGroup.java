package com.github.yingzhuo.bayonet.inject;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

/**
 * 注入 {@code spring.application.group} 配置属性的注解。
 *
 * <pre>{@code
 * &#064;SpringApplicationGroup
 * private String group;
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${spring.application.group}")
public @interface SpringApplicationGroup {
}

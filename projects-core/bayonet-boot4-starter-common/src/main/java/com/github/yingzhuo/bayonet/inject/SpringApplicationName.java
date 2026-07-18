package com.github.yingzhuo.bayonet.inject;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

/**
 * 注入 {@code spring.application.name} 配置属性的注解。
 *
 * <pre>{@code
 * &#064;SpringApplicationName
 * private String name;
 * }</pre>
 * @author 应卓
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${spring.application.name}")
public @interface SpringApplicationName {
}

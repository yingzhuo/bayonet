package com.github.yingzhuo.bayonet.bean;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

/**
 * 注入 {@code spring.application.name} 配置属性的注解。
 *
 * <pre>{@code
 * @SpringApplicationName
 * private String name;
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${spring.application.name}")
public @interface SpringApplicationName {
}

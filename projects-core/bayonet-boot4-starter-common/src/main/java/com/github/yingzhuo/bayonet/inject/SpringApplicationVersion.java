package com.github.yingzhuo.bayonet.inject;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

/**
 * 注入 {@code spring.application.version} 配置属性的注解。
 *
 * <pre>{@code
 * &#064;SpringApplicationVersion
 * private String version;
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${spring.application.version}")
public @interface SpringApplicationVersion {
}

package com.github.yingzhuo.bayonet.inject;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

/**
 * 注入 {@code spring.application.pid} 配置属性的注解。
 * @author 应卓
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${spring.application.pid:-1}")
public @interface SpringApplicationPID {
}

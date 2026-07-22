package com.github.yingzhuo.bayonet.webmvc.support.arg;

import java.lang.annotation.*;

/**
 * 标记控制器方法参数以注入客户端 IP 地址的注解。
 * <p>配合 {@link RemoteAddressHandlerMethodArgumentResolver} 使用，
 * 支持 {@link String} 和 {@link java.util.Optional Optional&lt;String&gt;} 两种参数类型。</p>
 *
 * <pre>{@code
 * @GetMapping("/example")
 * public String example(@RemoteAddress String ip) {
 *     return ip;
 * }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteAddress {
}

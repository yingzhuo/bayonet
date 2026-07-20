package com.github.yingzhuo.bayonet.webcli.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用自动注册信任所有证书的 {@link org.springframework.http.client.ClientHttpRequestFactory ClientHttpRequestFactory} Bean。
 *
 * <p>使用此注解后，Spring 容器会自动注册名为 {@code "unsafeClientHttpRequestFactory"} 的
 * {@link org.springframework.http.client.ClientHttpRequestFactory ClientHttpRequestFactory} Bean，
 * 该工厂信任所有 SSL/TLS 证书（含自签名证书），并禁用主机名验证以支持域名不匹配的 HTTPS 端点。</p>
 *
 * <p>配合 {@link UnsafeClientHttpRequestFactory @UnsafeClientHttpRequestFactory} 注解注入：</p>
 * <pre>{@code
 * &#64;Configuration
 * &#64;ImportUnsafeClientHttpRequestFactory
 * public class AppConfig {
 *
 *     &#64;Bean
 *     public WebClient webClient(
 *             &#64;UnsafeClientHttpRequestFactory ClientHttpRequestFactory factory) {
 *         return WebClient.builder()
 *                 .clientConnector(new ReactorClientHttpConnector(factory))
 *                 .build();
 *     }
 * }
 * }</pre>
 *
 * @author 应卓
 * @see UnsafeClientHttpRequestFactory
 * @see ImportUnsafeClientHttpRequestFactoryImporting
 * @since 4.1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Import(ImportUnsafeClientHttpRequestFactoryImporting.class)
public @interface ImportUnsafeClientHttpRequestFactory {

    /**
     * 连接超时时间（毫秒）。
     * <p>设置与目标服务器建立 TCP 连接的最大等待时间。默认值为 10 秒（{@code 10_000} 毫秒）。</p>
     *
     * @return 连接超时毫秒数，必须为正值
     */
    long connectTimeoutInMilliseconds() default 10_000L;

    /**
     * 读取响应超时时间（毫秒）。
     * <p>设置等待服务器返回响应的最大时间。默认值为 30 秒（{@code 30_000} 毫秒）。</p>
     *
     * @return 读取超时毫秒数，必须为正值
     */
    long readTimeoutInMilliseconds() default 30_000L;

}

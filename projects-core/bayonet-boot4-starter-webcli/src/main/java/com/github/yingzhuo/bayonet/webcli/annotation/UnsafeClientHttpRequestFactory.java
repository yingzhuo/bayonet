package com.github.yingzhuo.bayonet.webcli.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * 用于注入名为 {@code "unsafeClientHttpRequestFactory"} 的 {@link org.springframework.http.client.ClientHttpRequestFactory ClientHttpRequestFactory} Bean 的限定符注解。
 *
 * <p>配合 {@link ImportUnsafeClientHttpRequestFactory @ImportUnsafeClientHttpRequestFactory} 注解使用，
 * 可自动装配信任所有证书的 {@code WebClient}：</p>
 * <pre>{@code
 * &#64;Bean
 * public WebClient webClient(
 *         &#64;UnsafeClientHttpRequestFactory ClientHttpRequestFactory factory) {
 *     return WebClient.builder()
 *             .clientConnector(new ReactorClientHttpConnector(factory))
 *             .build();
 * }
 * }</pre>
 *
 * @author 应卓
 * @see ImportUnsafeClientHttpRequestFactory
 * @since 4.1.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier(ImportUnsafeClientHttpRequestFactoryImporting.BEAN_NAME)
public @interface UnsafeClientHttpRequestFactory {
}

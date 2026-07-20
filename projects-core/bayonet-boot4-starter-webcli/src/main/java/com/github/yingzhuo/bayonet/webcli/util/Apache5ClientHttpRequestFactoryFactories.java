package com.github.yingzhuo.bayonet.webcli.util;

import com.github.yingzhuo.bayonet.webcli.factory.Apache5ClientHttpRequestFactoryBean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.security.KeyStore;
import java.time.Duration;

/**
 * Apache HttpClient 5 的 {@link HttpComponentsClientHttpRequestFactory} 工厂工具类。
 *
 * <p>提供静态便捷方法快速创建 {@link HttpComponentsClientHttpRequestFactory} 实例，
 * 底层委托给 {@link Apache5ClientHttpRequestFactoryBean} 完成配置和生命周期管理。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * // 默认工厂
 * var factory = Apache5ClientHttpRequestFactoryFactories.createDefault();
 *
 * // 信任所有证书（含自签名，域名不匹配时也信任）
 * var factory = Apache5ClientHttpRequestFactoryFactories.createUnsafe();
 *
 * // 自定义配置
 * var factory = Apache5ClientHttpRequestFactoryFactories.create(
 *     false, myTrustStore, myTrustStrategy, myKeyStore, "password",
 *     Duration.ofSeconds(5), Duration.ofSeconds(30));
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Apache5ClientHttpRequestFactoryFactories {

    /**
     * 创建默认的 {@link HttpComponentsClientHttpRequestFactory}。
     * <p>使用 JDK 默认信任库和 Apache HttpClient 5 默认连接池配置。</p>
     *
     * @return HttpComponentsClientHttpRequestFactory 实例
     * @throws BeanCreationException 创建失败时抛出
     */
    public static HttpComponentsClientHttpRequestFactory createDefault() {
        try {
            var factory = new Apache5ClientHttpRequestFactoryBean();
            factory.afterPropertiesSet();
            return (HttpComponentsClientHttpRequestFactory) factory.getObject();
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create Apache5 ClientHttpRequestFactory", e);
        }
    }

    /**
     * 创建信任所有证书的 {@link HttpComponentsClientHttpRequestFactory}。
     * <p>适用于需要访问自签名证书且域名不匹配的 HTTPS 端点。
     * 连接池请求超时默认为 10 秒，读取超时默认为 30 秒。</p>
     *
     * @return HttpComponentsClientHttpRequestFactory 实例
     * @throws BeanCreationException 创建失败时抛出
     */
    public static HttpComponentsClientHttpRequestFactory createUnsafe() {
        return create(true, null, null, null, null,
                Duration.ofSeconds(10), Duration.ofSeconds(30));
    }

    /**
     * 创建完全自定义的 {@link HttpComponentsClientHttpRequestFactory}。
     *
     * <p><b>参数优先级</b></p>
     * <ul>
     *   <li>若 {@code trustStore} 和 {@code trustStrategy} 均不为 {@code null}，
     *       则使用自定义信任库和策略做证书校验，此时 {@code trustAll} 对证书链校验无影响</li>
     *   <li>若 {@code trustStore} 和 {@code trustStrategy} 均为 {@code null} 且
     *       {@code trustAll} 为 {@code true}，则信任所有证书（含自签名），且禁用主机名验证</li>
     *   <li>否则使用 JDK 默认信任库</li>
     * </ul>
     *
     * @param trustAll                   是否信任所有证书
     * @param trustStore                 自定义信任库，需配合 {@code trustStrategy} 使用（可为 {@code null}）
     * @param trustStrategy              自定义信任策略，需配合 {@code trustStore} 使用（可为 {@code null}）
     * @param clientSideKeyStore         客户端证书密钥库，用于 mTLS（可为 {@code null}）
     * @param clientSideKeyStorePassword 客户端证书密钥库密码（可为 {@code null}）
     * @param connectionRequestTimeout   从连接池获取连接的超时时间（可为 {@code null}）
     * @param readTimeout                读取超时时间（可为 {@code null}）
     * @return HttpComponentsClientHttpRequestFactory 实例
     * @throws BeanCreationException     创建失败时抛出
     * @throws IllegalArgumentException  客户端证书密钥库/密码或信任库/策略配置不一致时抛出
     */
    public static HttpComponentsClientHttpRequestFactory create(
            boolean trustAll,
            @Nullable KeyStore trustStore,
            @Nullable TrustStrategy trustStrategy,
            @Nullable KeyStore clientSideKeyStore,
            @Nullable String clientSideKeyStorePassword,
            @Nullable Duration connectionRequestTimeout,
            @Nullable Duration readTimeout) {

        try {
            var factory = new Apache5ClientHttpRequestFactoryBean();
            factory.setTrustAll(trustAll);
            factory.setTrustStore(trustStore);
            factory.setTrustStrategy(trustStrategy);
            factory.setClientSideKeyStore(clientSideKeyStore);
            factory.setClientSideKeyStorePassword(clientSideKeyStorePassword);
            factory.setConnectionRequestTimeout(connectionRequestTimeout);
            factory.setReadTimeout(readTimeout);
            factory.afterPropertiesSet();
            return (HttpComponentsClientHttpRequestFactory) factory.getObject();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create Apache5 ClientHttpRequestFactory", e);
        }
    }
}

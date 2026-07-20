package com.github.yingzhuo.bayonet.webcli.util;

import com.github.yingzhuo.bayonet.webcli.factory.JdkClientHttpRequestFactoryBean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.security.KeyStore;
import java.time.Duration;

/**
 * JDK {@link java.net.http.HttpClient} 的 {@link JdkClientHttpRequestFactory} 工厂工具类。
 *
 * <p>提供静态便捷方法快速创建 {@link JdkClientHttpRequestFactory} 实例，
 * 底层委托给 {@link JdkClientHttpRequestFactoryBean} 完成配置和生命周期管理。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * // 默认工厂
 * var factory = JdkClientHttpRequestFactoryFactories.createDefault();
 *
 * // 信任所有证书（含自签名，域名不匹配时也信任）
 * var factory = JdkClientHttpRequestFactoryFactories.createUnsafe();
 *
 * // 自定义配置
 * var factory = JdkClientHttpRequestFactoryFactories.create(
 *     false, myTrustStore, myKeyStore, "password",
 *     Duration.ofSeconds(5), Duration.ofSeconds(30));
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JdkClientHttpRequestFactoryFactories {

    /**
     * 创建默认的 {@link JdkClientHttpRequestFactory}。
     * <p>使用 JDK 默认信任库和默认超时配置。</p>
     *
     * @return JdkClientHttpRequestFactory 实例
     * @throws BeanCreationException 创建失败时抛出
     */
    public static JdkClientHttpRequestFactory createDefault() {
        try {
            var factory = new JdkClientHttpRequestFactoryBean();
            factory.afterPropertiesSet();
            return (JdkClientHttpRequestFactory) factory.getObject();
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create JDK ClientHttpRequestFactory", e);
        }
    }

    /**
     * 创建信任所有证书的 {@link JdkClientHttpRequestFactory}。
     * <p>适用于需要访问自签名证书且域名不匹配的 HTTPS 端点。
     * 连接超时默认为 10 秒，读取超时默认为 30 秒。</p>
     *
     * @return JdkClientHttpRequestFactory 实例
     * @throws BeanCreationException 创建失败时抛出
     */
    public static JdkClientHttpRequestFactory createUnsafe() {
        return create(true, null, null, null, Duration.ofSeconds(10), Duration.ofSeconds(30));
    }

    /**
     * 创建完全自定义的 {@link JdkClientHttpRequestFactory}。
     *
     * <p><b>参数优先级</b></p>
     * <ul>
     *   <li>若 {@code trustStore} 不为 {@code null}，则使用自定义信任库做证书校验，
     *       此时 {@code trustAll} 对证书链校验无影响</li>
     *   <li>若 {@code trustStore} 为 {@code null} 且 {@code trustAll} 为 {@code true}，
     *       则信任所有证书（含自签名），且禁用主机名验证以支持域名不匹配</li>
     *   <li>否则使用 JDK 默认信任库</li>
     * </ul>
     *
     * @param trustAll                   是否信任所有证书
     * @param trustStore                 自定义信任库（可为 {@code null}）
     * @param clientSideKeyStore         客户端证书密钥库，用于 mTLS（可为 {@code null}）
     * @param clientSideKeyStorePassword 客户端证书密钥库密码（可为 {@code null}）
     * @param connectTimeout             连接超时（可为 {@code null}）
     * @param readTimeout                读取超时（可为 {@code null}）
     * @return JdkClientHttpRequestFactory 实例
     * @throws BeanCreationException    创建失败时抛出
     * @throws IllegalArgumentException 客户端证书密钥库与密码配置不一致时抛出
     */
    public static JdkClientHttpRequestFactory create(
            boolean trustAll,
            @Nullable KeyStore trustStore,
            @Nullable KeyStore clientSideKeyStore,
            @Nullable String clientSideKeyStorePassword,
            @Nullable Duration connectTimeout,
            @Nullable Duration readTimeout) {

        try {
            var factory = new JdkClientHttpRequestFactoryBean();
            factory.setTrustAll(trustAll);
            factory.setTrustStore(trustStore);
            factory.setClientSideKeyStore(clientSideKeyStore);
            factory.setClientSideKeyStorePassword(clientSideKeyStorePassword);
            factory.setConnectTimeout(connectTimeout);
            factory.setReadTimeout(readTimeout);
            factory.afterPropertiesSet();
            return (JdkClientHttpRequestFactory) factory.getObject();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create JDK ClientHttpRequestFactory", e);
        }
    }
}

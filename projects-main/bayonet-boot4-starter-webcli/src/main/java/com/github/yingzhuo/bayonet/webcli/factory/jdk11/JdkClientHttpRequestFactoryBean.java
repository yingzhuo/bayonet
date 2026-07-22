package com.github.yingzhuo.bayonet.webcli.factory.jdk11;

import com.github.yingzhuo.bayonet.webcli.factory.AbstractClientHttpRequestFactoryBean;
import nl.altindag.ssl.SSLFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 基于 JDK {@link HttpClient} 和 {@link SSLFactory} 的 {@link ClientHttpRequestFactory} 工厂 Bean。
 *
 * <p>通过 {@link SSLFactory} 配置 SSL/TLS 行为（信任库、客户端证书、主机名验证等），
 * 通过构造器注入确保 Bean 实例化后不可变。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * // 信任所有证书
 * @Bean
 * public JdkClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     var sslFactory = SSLFactory.builder()
 *             .withUnsafeTrustMaterial()
 *             .build();
 *     return new JdkClientHttpRequestFactoryBean(sslFactory);
 * }
 *
 * // 默认配置
 * @Bean
 * public JdkClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     return new JdkClientHttpRequestFactoryBean();
 * }
 * }</pre>
 *
 * @author 应卓
 * @see SSLFactory
 * @see JdkClientHttpRequestFactory
 * @since 4.1.0
 */
public class JdkClientHttpRequestFactoryBean extends AbstractClientHttpRequestFactoryBean {

    /**
     * 创建默认的工厂 Bean。
     * <p>使用 JDK 默认信任材料，连接超时 10 秒，读取超时 30 秒。</p>
     */
    public JdkClientHttpRequestFactoryBean() {
        super();
    }

    /**
     * 创建指定 SSL 配置的工厂 Bean。
     * <p>使用默认超时配置（连接超时 10 秒，读取超时 30 秒）。</p>
     *
     * @param sslFactory SSL 配置工厂，不能为 {@code null}
     */
    public JdkClientHttpRequestFactoryBean(SSLFactory sslFactory) {
        super(sslFactory);
    }

    /**
     * 创建完全自定义的工厂 Bean。
     *
     * @param sslFactory      SSL 配置工厂，不能为 {@code null}
     * @param connectTimeout  连接超时（可为 {@code null}，零或负值不允许）
     * @param readTimeout     读取超时（可为 {@code null}，零或负值不允许）
     * @throws IllegalArgumentException sslFactory 为 {@code null}，或超时值为零/负数时抛出
     */
    public JdkClientHttpRequestFactoryBean(SSLFactory sslFactory, @Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        super(sslFactory, connectTimeout, readTimeout);
    }

    /**
     * 创建并返回 {@link ClientHttpRequestFactory} 实例。
     *
     * <p>通过 {@link SSLFactory#getSslContext()} 和 {@link SSLFactory#getSslParameters()}
     * 获取配置好的 SSL 上下文和参数，构建 JDK {@link HttpClient} 实例。</p>
     *
     * @return 配置好的 {@link JdkClientHttpRequestFactory} 实例
     */
    @Override
    public ClientHttpRequestFactory getObject() {
        var clientBuilder = HttpClient.newBuilder()
                .sslContext(sslFactory.getSslContext())
                .sslParameters(sslFactory.getSslParameters());

        if (connectTimeout != null) {
            clientBuilder.connectTimeout(connectTimeout);
        }

        var factory = new JdkClientHttpRequestFactory(clientBuilder.build());

        if (readTimeout != null) {
            factory.setReadTimeout(readTimeout);
        }

        return factory;
    }
}

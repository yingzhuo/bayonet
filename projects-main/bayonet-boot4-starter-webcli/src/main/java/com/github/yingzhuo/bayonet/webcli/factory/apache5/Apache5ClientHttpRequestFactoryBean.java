package com.github.yingzhuo.bayonet.webcli.factory.apache5;

import com.github.yingzhuo.bayonet.utility.CloseUtils;
import com.github.yingzhuo.bayonet.webcli.factory.AbstractClientHttpRequestFactoryBean;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.apache5.util.Apache5SslUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.jspecify.annotations.Nullable;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 基于 Apache HttpClient 5 和 {@link SSLFactory} 的 {@link ClientHttpRequestFactory} 工厂 Bean。
 *
 * <p>通过 {@link SSLFactory} 配置 SSL/TLS 行为（信任库、客户端证书、主机名验证等），
 * 通过构造器注入确保 Bean 实例化后不可变。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * // 默认配置
 * @Bean
 * public Apache5ClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     return new Apache5ClientHttpRequestFactoryBean();
 * }
 *
 * // 自定义 SSL 和超时
 * @Bean
 * public Apache5ClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     var sslFactory = SSLFactory.builder()
 *             .withUnsafeTrustMaterial()
 *             .build();
 *     return new Apache5ClientHttpRequestFactoryBean(sslFactory,
 *             Duration.ofSeconds(5), Duration.ofSeconds(15));
 * }
 * }</pre>
 *
 * @author 应卓
 * @see SSLFactory
 * @see HttpComponentsClientHttpRequestFactory
 * @since 4.1.0
 */
public class Apache5ClientHttpRequestFactoryBean extends AbstractClientHttpRequestFactoryBean {

    private @Nullable HttpComponentsClientHttpRequestFactory requestFactory;
    private @Nullable PoolingHttpClientConnectionManager connectionManager;
    private @Nullable CloseableHttpClient httpClient;

    /**
     * 创建默认的工厂 Bean。
     * <p>使用 JDK 默认信任材料，连接超时 10 秒，读取超时 30 秒。</p>
     */
    public Apache5ClientHttpRequestFactoryBean() {
        super();
    }

    /**
     * 创建指定 SSL 配置的工厂 Bean。
     * <p>使用默认超时配置（连接超时 10 秒，读取超时 30 秒）。</p>
     *
     * @param sslFactory SSL 配置工厂，不能为 {@code null}
     */
    public Apache5ClientHttpRequestFactoryBean(SSLFactory sslFactory) {
        super(sslFactory);
    }

    /**
     * 创建完全自定义的工厂 Bean。
     *
     * @param sslFactory     SSL 配置工厂，不能为 {@code null}
     * @param connectTimeout 连接超时（可为 {@code null}，零或负值不允许）
     * @param readTimeout    读取超时（可为 {@code null}，零或负值不允许）
     * @throws IllegalArgumentException sslFactory 为 {@code null}，或超时值为零/负数时抛出
     */
    public Apache5ClientHttpRequestFactoryBean(SSLFactory sslFactory, @Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        super(sslFactory, connectTimeout, readTimeout);
    }

    /**
     * 初始化 HTTP 客户端和连接管理器。
     *
     * <p>在 Bean 属性设置完成后由 Spring 容器调用，构建 {@link CloseableHttpClient}、
     * {@link PoolingHttpClientConnectionManager} 和 {@link HttpComponentsClientHttpRequestFactory}。</p>
     */
    @Override
    public void afterPropertiesSet() {
        this.connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(Apache5SslUtils.toTlsSocketStrategy(sslFactory))
                .build();

        this.httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .build();

        this.requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.requestFactory.setConnectionRequestTimeout(connectTimeout);
        this.requestFactory.setReadTimeout(readTimeout);
    }

    /**
     * 返回 {@link ClientHttpRequestFactory} 实例。
     *
     * @return 配置好的 {@link HttpComponentsClientHttpRequestFactory} 实例
     * @throws IllegalStateException 在 {@link #afterPropertiesSet()} 尚未调用时抛出
     */
    @Override
    public ClientHttpRequestFactory getObject() {
        var factory = this.requestFactory;
        Assert.state(factory != null, "Apache5ClientHttpRequestFactoryBean is not initialized");
        return factory;
    }

    /**
     * 销毁时释放 HTTP 客户端和连接管理器资源。
     */
    @Override
    public void destroy() {
        CloseUtils.closeQuietly(httpClient);
        CloseUtils.closeQuietly(connectionManager);
    }
}

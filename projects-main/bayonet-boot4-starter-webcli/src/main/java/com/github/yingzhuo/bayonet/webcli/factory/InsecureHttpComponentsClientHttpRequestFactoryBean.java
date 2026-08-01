package com.github.yingzhuo.bayonet.webcli.factory;

import com.github.yingzhuo.bayonet.utility.CloseUtils;
import com.github.yingzhuo.bayonet.utility.net.SSLFactories;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 信任所有证书的 {@link HttpComponentsClientHttpRequestFactory} FactoryBean。
 *
 * <p>直接基于 {@link SSLFactories#createInsecure()} 构建 Apache HttpClient 5，
 * 使用 {@link NoopHostnameVerifier} 跳过主机名验证。
 * 实现 {@link DisposableBean}，容器销毁时自动释放底层资源，避免资源泄漏。</p>
 *
 * <p>仅建议在开发或测试环境中使用。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public final class InsecureHttpComponentsClientHttpRequestFactoryBean
        implements FactoryBean<HttpComponentsClientHttpRequestFactory>, DisposableBean {

    private final CloseableHttpClient httpClient;
    private final PoolingHttpClientConnectionManager connectionManager;
    private final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;

    /**
     * 使用默认超时创建实例
     */
    public InsecureHttpComponentsClientHttpRequestFactoryBean() {
        this(null, null);
    }

    /**
     * 使用指定超时创建实例
     *
     * @param connectTimeout 连接超时，为 {@code null} 时使用 10 秒
     * @param readTimeout    读取超时，为 {@code null} 时使用 30 秒
     */
    public InsecureHttpComponentsClientHttpRequestFactoryBean(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {

        if (connectTimeout != null) {
            Assert.isTrue(!connectTimeout.isZero() && !connectTimeout.isNegative(), "connect timeout must be a positive duration");
        }

        if (readTimeout != null) {
            Assert.isTrue(!readTimeout.isZero() && !readTimeout.isNegative(), "read timeout must be a positive duration");
        }

        connectTimeout = Objects.requireNonNullElse(connectTimeout, Duration.ofSeconds(10));
        readTimeout = Objects.requireNonNullElse(readTimeout, Duration.ofSeconds(30));

        var sslContext = SSLFactories.createInsecure().getRequiredLeft();
        var tlsStrategy = new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE);

        var cmBuilder = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy)
                .setDefaultConnectionConfig(
                        ConnectionConfig.custom()
                                .setConnectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS).build()
                );

        this.connectionManager = cmBuilder.build();

        this.httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();

        var factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(readTimeout);

        this.httpComponentsClientHttpRequestFactory = factory;
    }

    /**
     * 获取 {@link HttpComponentsClientHttpRequestFactory} 实例。
     *
     * @return {@link HttpComponentsClientHttpRequestFactory}（非 {@code null}）
     */
    @Override
    public HttpComponentsClientHttpRequestFactory getObject() {
        return this.httpComponentsClientHttpRequestFactory;
    }

    /**
     * 返回工厂创建的对象的类型。
     *
     * @return {@link HttpComponentsClientHttpRequestFactory} 的 {@link Class}
     */
    @Override
    public Class<?> getObjectType() {
        return HttpComponentsClientHttpRequestFactory.class;
    }

    /**
     * 容器销毁时释放底层 HTTP 客户端及连接管理器资源。
     */
    @Override
    public void destroy() {
        CloseUtils.closeQuietly(this.httpClient);
        CloseUtils.closeQuietly(this.connectionManager);
    }

}

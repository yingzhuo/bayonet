package com.github.yingzhuo.bayonet.webcli.factory;

import com.github.yingzhuo.bayonet.utility.net.SSLContextFactories;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.jspecify.annotations.Nullable;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Apache HttpClient 5 的 {@link HttpComponentsClientHttpRequestFactory} 工厂工具类。
 *
 * <p>提供基于 Apache HttpClient 5 的安全/不安全 HTTP 请求工厂创建方法。
 * 此模块需依赖 {@code org.apache.httpcomponents.client5:httpclient5}。</p>
 *
 * @author 应卓
 * @see SSLContextFactories
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Apache5RequestFactoryUtils {

    /**
     * 创建信任所有证书的 {@link HttpComponentsClientHttpRequestFactory}。
     * <p>直接基于 {@link SSLContextFactories#createInsecure()} 构建 Apache HttpClient 5，
     * 使用 {@link NoopHostnameVerifier} 跳过主机名验证。</p>
     * <p>仅建议在开发或测试环境中使用。</p>
     *
     * @return {@link HttpComponentsClientHttpRequestFactory}（非 {@code null}）
     */
    public static HttpComponentsClientHttpRequestFactory createInsecure() {
        return createInsecure(null, null);
    }

    /**
     * 创建信任所有证书的 {@link HttpComponentsClientHttpRequestFactory}。
     * <p>直接基于 {@link SSLContextFactories#createInsecure()} 构建 Apache HttpClient 5，
     * 使用 {@link NoopHostnameVerifier} 跳过主机名验证。</p>
     * <p>仅建议在开发或测试环境中使用。</p>
     * <p>注意: 本方法的产品要放在Spring上下文之中，以防资源泄露。</p>
     *
     * @param connectTimeout 连接超时，为 {@code null} 时 Apache 使用默认值
     * @param readTimeout    读取超时，为 {@code null} 时不设置
     * @return {@link HttpComponentsClientHttpRequestFactory}（非 {@code null}）
     */
    public static HttpComponentsClientHttpRequestFactory createInsecure(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        var sslContext = SSLContextFactories.createInsecure();
        var tlsStrategy = new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE);

        var cmBuilder = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy);

        if (connectTimeout != null) {
            cmBuilder.setDefaultConnectionConfig(
                    ConnectionConfig.custom()
                            .setConnectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS).build()
            );
        }

        var httpClient = HttpClientBuilder.create()
                .setConnectionManager(cmBuilder.build())
                .build();

        var factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        if (readTimeout != null) {
            factory.setReadTimeout(readTimeout);
        }

        return factory;
    }
}

package com.github.yingzhuo.bayonet.webcli.util;

import com.github.yingzhuo.bayonet.utility.net.SSLContextFactories;
import com.github.yingzhuo.bayonet.utility.ssl.SslBundleFactories;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.Assert;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

/**
 * {@link ClientHttpRequestFactory} 工厂工具类。
 *
 * <p>提供创建预配置的 HTTP 请求工厂的静态便捷方法，
 * 支持自定义 SSL 捆绑和超时设置。</p>
 *
 * @author 应卓
 * @see SslBundleFactories
 * @see com.github.yingzhuo.bayonet.utility.net.SSLContextFactories
 * @see ClientHttpRequestFactoryBuilder
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestFactoryUtils {

    /**
     * 使用指定 {@link SslBundle} 创建 {@link ClientHttpRequestFactory}。
     * <p>超时使用默认值：connect 10 秒，read 30 秒。</p>
     *
     * @param sslBundle SSL 捆绑（非 {@code null}）
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static ClientHttpRequestFactory create(SslBundle sslBundle) {
        return create(sslBundle, null, null);
    }

    /**
     * 使用默认的 {@link SslBundle} 及自定义超时创建 {@link ClientHttpRequestFactory}。
     *
     * @param connectTimeout 连接超时，为 {@code null} 时使用 10 秒
     * @param readTimeout    读取超时，为 {@code null} 时使用 30 秒
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static ClientHttpRequestFactory create(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        return create(SslBundleFactories.createDefault(), connectTimeout, readTimeout);
    }

    /**
     * 使用指定 {@link SslBundle} 及自定义超时创建 {@link ClientHttpRequestFactory}。
     *
     * @param sslBundle      SSL 捆绑（非 {@code null}）
     * @param connectTimeout 连接超时，为 {@code null} 时使用 10 秒
     * @param readTimeout    读取超时，为 {@code null} 时使用 30 秒
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static ClientHttpRequestFactory create(SslBundle sslBundle, @Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        Assert.notNull(sslBundle, "sslBundle must not be null");

        connectTimeout = Objects.requireNonNullElse(connectTimeout, Duration.ofSeconds(10));
        readTimeout = Objects.requireNonNullElse(readTimeout, Duration.ofSeconds(30));

        var settings =
                HttpClientSettings.ofSslBundle(sslBundle)
                        .withConnectTimeout(connectTimeout)
                        .withReadTimeout(readTimeout);

        return ClientHttpRequestFactoryBuilder.detect()
                .build(settings);
    }

    /**
     * 创建信任所有证书的 {@link ClientHttpRequestFactory}。
     * <p>内部使用 {@link SslBundleFactories#createInsecure()}，
     * 仅建议在开发或测试环境中使用。</p>
     *
     * @param connectTimeout 连接超时，为 {@code null} 时使用 10 秒
     * @param readTimeout    读取超时，为 {@code null} 时使用 30 秒
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static ClientHttpRequestFactory createInsecure(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        return create(SslBundleFactories.createInsecure(), connectTimeout, readTimeout);
    }

    // ------

    /**
     * 创建信任所有证书的 {@link JdkClientHttpRequestFactory}。
     * <p>内部使用 {@link SslBundleFactories#createInsecure()}，
     * 仅建议在开发或测试环境中使用。</p>
     *
     * @param connectTimeout 连接超时，为 {@code null} 时使用 10 秒
     * @param readTimeout    读取超时，为 {@code null} 时使用 30 秒
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static JdkClientHttpRequestFactory createInsecureJdk(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        var sslCtx = SSLContextFactories.createInsecure();
        var params = sslCtx.getDefaultSSLParameters();
        params.setEndpointIdentificationAlgorithm(null);

        var httpClient = HttpClient.newBuilder()
                .sslContext(sslCtx)
                .sslParameters(params)
                .build();
        return new JdkClientHttpRequestFactory(httpClient);
    }

}

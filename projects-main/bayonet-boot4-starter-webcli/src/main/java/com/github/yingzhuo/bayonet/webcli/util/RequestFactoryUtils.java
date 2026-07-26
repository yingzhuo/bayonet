package com.github.yingzhuo.bayonet.webcli.util;

import com.github.yingzhuo.bayonet.utility.net.SSLFactories;
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
 * @see SSLFactories
 * @see ClientHttpRequestFactoryBuilder
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestFactoryUtils {

    /**
     * 使用指定 {@link SslBundle} 创建 {@link ClientHttpRequestFactory}。
     * <p>超时使用默认值：connect 10 秒，read 30 秒。</p>
     *
     * @param sslBundle SSL 捆绑，为 {@code null} 时使用系统默认
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static ClientHttpRequestFactory createDetect(@Nullable SslBundle sslBundle) {
        return createDetect(sslBundle, null, null);
    }

    /**
     * 使用系统默认的 {@link SslBundle} 及自定义超时创建 {@link ClientHttpRequestFactory}。
     *
     * @param connectTimeout 连接超时，为 {@code null} 时使用 10 秒
     * @param readTimeout    读取超时，为 {@code null} 时使用 30 秒
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static ClientHttpRequestFactory createDetect(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        return createDetect(null, connectTimeout, readTimeout);
    }

    /**
     * 使用指定 {@link SslBundle} 及自定义超时创建 {@link ClientHttpRequestFactory}。
     *
     * @param sslBundle      SSL 捆绑，为 {@code null} 时使用系统默认
     * @param connectTimeout 连接超时，为 {@code null} 时使用 10 秒
     * @param readTimeout    读取超时，为 {@code null} 时使用 30 秒
     * @return {@link ClientHttpRequestFactory}（非 {@code null}）
     */
    public static ClientHttpRequestFactory createDetect(@Nullable SslBundle sslBundle, @Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        if (connectTimeout != null) {
            Assert.isTrue(!connectTimeout.isZero() && !connectTimeout.isNegative(), "connect timeout must be a positive duration");
        }

        if (readTimeout != null) {
            Assert.isTrue(!readTimeout.isZero() && !readTimeout.isNegative(), "read timeout must be a positive duration");
        }

        sslBundle = Objects.requireNonNullElseGet(sslBundle, SslBundle::systemDefault);
        connectTimeout = Objects.requireNonNullElse(connectTimeout, Duration.ofSeconds(10));
        readTimeout = Objects.requireNonNullElse(readTimeout, Duration.ofSeconds(30));

        var settings =
                HttpClientSettings.ofSslBundle(sslBundle)
                        .withConnectTimeout(connectTimeout)
                        .withReadTimeout(readTimeout);

        return ClientHttpRequestFactoryBuilder.detect()
                .build(settings);
    }

    // ------

    /**
     * 创建信任所有证书的 {@link JdkClientHttpRequestFactory}。
     * <p>直接基于 {@link SSLFactories#createInsecure()} 构建 JDK {@link HttpClient}，
     * 绕过 Spring Boot 的 {@code SslBundle} 基础设施，避免中间层对主机名校验的干扰。</p>
     * <p>仅建议在开发或测试环境中使用。</p>
     *
     * @return {@link JdkClientHttpRequestFactory}（非 {@code null}）
     */
    public static JdkClientHttpRequestFactory createInsecureJdk() {
        return createInsecureJdk(null, null);
    }

    /**
     * 创建信任所有证书的 {@link JdkClientHttpRequestFactory}。
     * <p>直接基于 {@link SSLFactories#createInsecure()} 构建 JDK {@link HttpClient}，
     * 绕过 Spring Boot 的 {@code SslBundle} 基础设施，避免中间层对主机名校验的干扰。</p>
     * <p>仅建议在开发或测试环境中使用。</p>
     *
     * @param connectTimeout 连接超时，为 {@code null} 时 JDK 使用默认值
     * @param readTimeout    读取超时，为 {@code null} 时不设置
     * @return {@link JdkClientHttpRequestFactory}（非 {@code null}）
     */
    public static JdkClientHttpRequestFactory createInsecureJdk(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {

        var ctxAndParameters = SSLFactories.createInsecure();
        var builder = HttpClient.newBuilder()
                .sslContext(ctxAndParameters.context())
                .sslParameters(ctxAndParameters.parameters());

        if (connectTimeout != null) {
            builder.connectTimeout(connectTimeout);
        }

        var httpClient = builder.build();
        var factory = new JdkClientHttpRequestFactory(httpClient);

        if (readTimeout != null) {
            factory.setReadTimeout(readTimeout);
        }

        return factory;
    }

}

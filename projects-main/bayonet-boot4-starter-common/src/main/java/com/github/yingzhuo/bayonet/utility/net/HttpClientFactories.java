package com.github.yingzhuo.bayonet.utility.net;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.http.HttpClient;

/**
 * JDK {@link HttpClient} 工厂类。
 *
 * <p>提供创建预配置 HTTP 客户端的静态便捷方法，如跳过 SSL 校验的不安全客户端。</p>
 *
 * @author 应卓
 * @see SSLContextFactories
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpClientFactories {

    /**
     * 创建一个信任所有证书的 {@link HttpClient}。
     * <p>内部使用 {@link SSLContextFactories#createInsecureSSLContext()} 禁用服务端证书校验，
     * 仅建议在开发或测试环境中使用。</p>
     *
     * @return 不安全的 {@link HttpClient}（非 {@code null}）
     */
    public static HttpClient createInsecureHttpClient() {
        var sslContext = SSLContextFactories.createInsecureSSLContext();
        var parameters = sslContext.getDefaultSSLParameters();
        parameters.setEndpointIdentificationAlgorithm(null);
        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(parameters)
                .build();
    }
}

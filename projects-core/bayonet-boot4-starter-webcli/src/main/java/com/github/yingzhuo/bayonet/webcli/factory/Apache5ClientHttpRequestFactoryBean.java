package com.github.yingzhuo.bayonet.webcli.factory;

import lombok.Setter;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import java.security.*;
import java.time.Duration;

/**
 * 基于 Apache HttpClient 5 的 {@link ClientHttpRequestFactory} 工厂 Bean。
 *
 * <p>通过该工厂 Bean 创建的 {@link ClientHttpRequestFactory} 可用于构建 {@code WebClient}，
 * 支持 HTTP 和 HTTPS 协议，并可配置自定义 SSL 上下文。</p>
 *
 * <p><b>功能特性</b></p>
 * <ul>
 *   <li>HTTPS（标准证书 &amp; 自签名证书）— 通过 {@link #trustAll} 或自定义 {@link #trustStore}/{@link #trustStrategy}</li>
 *   <li>客户端证书认证（mTLS）— 通过 {@link #clientSideKeyStore}/{@link #clientSideKeyStorePassword}</li>
 *   <li>请求超时 &amp; 读取超时 — 通过 {@link #connectionRequestTimeout}/{@link #readTimeout}</li>
 * </ul>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * &#64;Bean
 * public Apache5ClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     var bean = new Apache5ClientHttpRequestFactoryBean();
 *     bean.setTrustAll(true);
 *     bean.setReadTimeout(Duration.ofSeconds(30));
 *     return bean;
 * }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Setter
public class Apache5ClientHttpRequestFactoryBean implements FactoryBean<ClientHttpRequestFactory>, InitializingBean {

    /**
     * 是否信任所有证书（包括自签名证书）。
     * <p>当 {@link #trustStore} 和 {@link #trustStrategy} 均未设置时生效。</p>
     */
    private boolean trustAll = false;

    /**
     * 自定义信任库。
     * <p>需与 {@link #trustStrategy} 配合使用。</p>
     */
    private @Nullable KeyStore trustStore;

    /**
     * 自定义信任策略。
     * <p>需与 {@link #trustStore} 配合使用。</p>
     */
    private @Nullable TrustStrategy trustStrategy;

    /**
     * 客户端证书密钥库（用于 mTLS 双向认证）。
     */
    private @Nullable KeyStore clientSideKeyStore;

    /**
     * 客户端证书密钥库密码。
     * <p>当 {@link #clientSideKeyStore} 不为 {@code null} 时必填。</p>
     */
    private @Nullable String clientSideKeyStorePassword;

    /**
     * 从连接池获取连接的超时时间。
     */
    private @Nullable Duration connectionRequestTimeout;

    /**
     * 读取响应数据的超时时间。
     */
    private @Nullable Duration readTimeout;

    @Override
    public ClientHttpRequestFactory getObject() throws Exception {
        var sslCtx = createSSLContext();

        var tlsStrategy = ClientTlsStrategyBuilder.create()
                .setSslContext(sslCtx)
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .buildClassic();

        var cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy)
                .build();

        var httpClient = HttpClientBuilder.create()
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .build();

        var factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        if (this.connectionRequestTimeout != null) {
            factory.setConnectionRequestTimeout(this.connectionRequestTimeout);
        }
        if (this.readTimeout != null) {
            factory.setReadTimeout(this.readTimeout);
        }

        return factory;
    }

    @Override
    public Class<?> getObjectType() {
        return ClientHttpRequestFactory.class;
    }

    @Override
    public void afterPropertiesSet() {
        // 若成对参数中任一为 null，将两者均置为 null 以保持状态一致
        if (this.clientSideKeyStore == null || this.clientSideKeyStorePassword == null) {
            this.clientSideKeyStore = null;
            this.clientSideKeyStorePassword = null;
        }
        if (this.trustStore == null || this.trustStrategy == null) {
            this.trustStore = null;
            this.trustStrategy = null;
        }
    }

    /**
     * 创建 SSL 上下文。
     *
     * <p>按以下优先级构建信任材料：</p>
     * <ol>
     *   <li>若 {@link #trustStore} 和 {@link #trustStrategy} 均已设置，则使用自定义信任库和策略</li>
     *   <li>否则若 {@link #trustAll} 为 {@code true}，则信任所有证书（含自签名证书）</li>
     *   <li>否则使用 JDK 默认信任库</li>
     * </ol>
     *
     * <p>若 {@link #clientSideKeyStore} 已设置，同时加载客户端证书（用于 mTLS 双向认证）。</p>
     *
     * @return 配置好的 {@link SSLContext}
     */
    private SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        boolean trustStoreSet = false;
        var builder = SSLContextBuilder.create();

        if (trustStore != null && trustStrategy != null) {
            builder.loadTrustMaterial(trustStore, trustStrategy);
            trustStoreSet = true;
        }

        if (clientSideKeyStore != null && clientSideKeyStorePassword != null) {
            builder.loadKeyMaterial(this.clientSideKeyStore, clientSideKeyStorePassword.toCharArray());
        }

        if (!trustStoreSet && trustAll) {
            builder.loadTrustMaterial(TrustAllStrategy.INSTANCE);
        }

        return builder.build();
    }
}

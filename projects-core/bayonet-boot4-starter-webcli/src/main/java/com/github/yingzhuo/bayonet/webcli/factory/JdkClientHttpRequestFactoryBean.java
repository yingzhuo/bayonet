package com.github.yingzhuo.bayonet.webcli.factory;

import com.github.yingzhuo.bayonet.webcli.support.TrustAllTrustManager;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import javax.net.ssl.*;
import java.net.http.HttpClient;
import java.security.*;
import java.time.Duration;

/**
 * 基于 JDK {@link HttpClient} 的 {@link ClientHttpRequestFactory} 工厂 Bean。
 *
 * <p>通过该工厂 Bean 创建的 {@link ClientHttpRequestFactory} 可用于构建 {@code WebClient}，
 * 支持 HTTP 和 HTTPS 协议，并可配置自定义 SSL 上下文。</p>
 *
 * <p><b>功能特性</b></p>
 * <ul>
 *   <li>HTTPS（标准证书 &amp; 自签名证书）— 通过 {@link #trustAll} 或自定义 {@link #trustStore}</li>
 *   <li>客户端证书认证（mTLS）— 通过 {@link #clientSideKeyStore}/{@link #clientSideKeyStorePassword}</li>
 *   <li>连接超时 &amp; 读取超时 — 通过 {@link #connectTimeout}/{@link #readTimeout}</li>
 * </ul>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * &#64;Bean
 * public JdkClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     var bean = new JdkClientHttpRequestFactoryBean();
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
public class JdkClientHttpRequestFactoryBean extends AbstractClientHttpRequestFactoryBean implements InitializingBean {

    /**
     * 是否信任所有证书（包括自签名证书）。
     * <p>当 {@code true} 且未设置自定义 {@link #trustStore} 时，同时禁用主机名验证
     * 以支持域名不匹配的自签名证书。</p>
     */
    private boolean trustAll = false;

    /**
     * 自定义信任库。
     */
    private @Nullable KeyStore trustStore;

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
     * 连接超时时间。
     */
    private @Nullable Duration connectTimeout;

    /**
     * 读取响应数据的超时时间。
     */
    private @Nullable Duration readTimeout;

    @Override
    public ClientHttpRequestFactory getObject() throws Exception {
        var sslCtx = createSSLContext();
        var builder = HttpClient.newBuilder()
                .sslContext(sslCtx);

        // trustAll 且未提供自定义 trustStore 时，禁用主机名验证
        // 以支持域名不匹配的自签名证书
        if (this.trustAll && this.trustStore == null) {
            var sslParams = SSLContext.getDefault().getDefaultSSLParameters();
            sslParams.setEndpointIdentificationAlgorithm(null);
            builder.sslParameters(sslParams);
        }

        if (this.connectTimeout != null) {
            builder.connectTimeout(this.connectTimeout);
        }

        var httpClient = builder.build();

        var factory = new JdkClientHttpRequestFactory(httpClient);
        if (this.readTimeout != null) {
            factory.setReadTimeout(this.readTimeout);
        }

        return factory;
    }

    @Override
    public void afterPropertiesSet() {
        // 校验客户端证书配置一致性：两个必须同时设置或同时不设置
        if (this.clientSideKeyStore == null && this.clientSideKeyStorePassword != null) {
            throw new IllegalArgumentException("clientSideKeyStore must not be null when clientSideKeyStorePassword is set");
        }
        if (this.clientSideKeyStore != null && this.clientSideKeyStorePassword == null) {
            throw new IllegalArgumentException("clientSideKeyStorePassword must not be null when clientSideKeyStore is set");
        }
    }

    /**
     * 创建 SSL 上下文。
     *
     * <p>按以下优先级构建信任材料：</p>
     * <ol>
     *   <li>若 {@link #trustStore} 已设置，则使用自定义信任库</li>
     *   <li>否则若 {@link #trustAll} 为 {@code true}，则信任所有证书（含自签名证书）</li>
     *   <li>否则使用 JDK 默认信任库</li>
     * </ol>
     *
     * <p>若 {@link #clientSideKeyStore} 已设置，同时加载客户端证书（用于 mTLS 双向认证）。</p>
     *
     * @return 配置好的 {@link SSLContext}
     */
    private SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        var ctx = SSLContext.getInstance("TLS");

        TrustManager[] trustManagers = null;
        if (trustStore != null) {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            trustManagers = tmf.getTrustManagers();
        }

        KeyManager[] keyManagers = null;
        if (this.clientSideKeyStore != null && this.clientSideKeyStorePassword != null) {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientSideKeyStore, clientSideKeyStorePassword.toCharArray());
            keyManagers = kmf.getKeyManagers();
        }

        if (trustManagers == null && this.trustAll) {
            trustManagers = new TrustManager[]{new TrustAllTrustManager()};
        }

        ctx.init(keyManagers, trustManagers, new SecureRandom());
        return ctx;
    }
}

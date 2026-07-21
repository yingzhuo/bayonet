package com.github.yingzhuo.bayonet.webcli.factory;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 基于 JDK {@link HttpClient} 的 {@link ClientHttpRequestFactory} 工厂 Bean。
 *
 * <p>继承自 {@link AbstractClientHttpRequestFactoryBean}，专注于配置连接超时和读取超时。
 * SSL/TLS 相关的配置（信任库、客户端证书、主机名验证等）由父类统一管理。</p>
 *
 * <p><b>功能特性</b></p>
 * <ul>
 *   <li>连接超时 — 通过 {@link #connectTimeout} 控制与目标服务器建立连接的超时时间</li>
 *   <li>读取超时 — 通过 {@link #readTimeout} 控制从服务器读取响应的超时时间</li>
 *   <li>SSL/TLS 配置 — 信任库、mTLS、主机名验证等继承自父类</li>
 * </ul>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * &#64;Bean
 * public JdkClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     var bean = new JdkClientHttpRequestFactoryBean();
 *     bean.setTrustAllIfNoTrustStore(true);
 *     bean.setReadTimeout(Duration.ofSeconds(30));
 *     return bean;
 * }
 * }</pre>
 *
 * @author 应卓
 * @see AbstractClientHttpRequestFactoryBean
 * @see JdkClientHttpRequestFactory
 * @since 4.1.0
 */
@Setter
public class JdkClientHttpRequestFactoryBean extends AbstractClientHttpRequestFactoryBean {

    /**
     * 连接超时时间。
     * <p>默认值为 10 秒。设为 {@code null} 则使用 JDK 默认超时。</p>
     */
    private @Nullable Duration connectTimeout = Duration.ofSeconds(10);

    /**
     * 读取响应数据的超时时间。
     * <p>默认值为 30 秒。设为 {@code null} 则使用 JDK 默认超时。</p>
     */
    private @Nullable Duration readTimeout = Duration.ofSeconds(30);

    /**
     * 创建并返回 {@link ClientHttpRequestFactory} 实例。
     *
     * <p>通过父类 {@link AbstractClientHttpRequestFactoryBean#createSSLContext()} 和
     * {@link AbstractClientHttpRequestFactoryBean#createSSLParameters()} 分别获取
     * SSL 上下文和参数，然后构建 JDK {@link HttpClient} 实例。当父类判定需要禁用主机名验证时
     * （信任所有证书且未使用自定义信任库），{@code createSSLParameters()} 返回的
     * {@link javax.net.ssl.SSLParameters SSLParameters} 中禁用了端点标识算法。</p>
     *
     * @return 配置好的 {@link JdkClientHttpRequestFactory} 实例
     * @throws Exception 创建 SSL 上下文或构建 HttpClient 时出错
     */
    @Override
    public ClientHttpRequestFactory getObject() throws Exception {
        var clientBuilder = HttpClient.newBuilder()
                .sslParameters(super.createSSLParameters())
                .sslContext(super.createSSLContext());

        if (this.connectTimeout != null) {
            clientBuilder.connectTimeout(this.connectTimeout);
        }

        var factory = new JdkClientHttpRequestFactory(clientBuilder.build());
        if (this.readTimeout != null) {
            factory.setReadTimeout(this.readTimeout);
        }

        return factory;
    }

}

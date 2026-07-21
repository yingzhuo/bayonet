package com.github.yingzhuo.bayonet.webcli.factory.jdk11;

import nl.altindag.ssl.SSLFactory;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

/**
 * 信任所有证书的 {@link JdkClientHttpRequestFactoryBean}。
 *
 * <p>预配置为信任所有 SSL/TLS 证书（含自签名证书）并禁用主机名验证，
 * 适用于访问自签名或域名不匹配的 HTTPS 端点。</p>
 *
 * <p><b>安全警告：</b>此工厂创建的 {@link org.springframework.http.client.ClientHttpRequestFactory ClientHttpRequestFactory}
 * 会跳过 SSL 证书链校验和主机名验证，仅在开发、测试或连接受控内网时使用。生产环境应优先使用
 * {@link JdkClientHttpRequestFactoryBean} 并配置自定义信任策略。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * @Bean
 * public UnsafeJdkClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     return new UnsafeJdkClientHttpRequestFactoryBean();
 * }
 * }</pre>
 *
 * @author 应卓
 * @see JdkClientHttpRequestFactoryBean
 * @see SSLFactory
 * @since 4.1.0
 */
public class UnsafeJdkClientHttpRequestFactoryBean extends JdkClientHttpRequestFactoryBean {

    /**
     * 创建默认的工厂 Bean。
     * <p>连接超时默认 10 秒，读取超时默认 30 秒。</p>
     */
    public UnsafeJdkClientHttpRequestFactoryBean() {
        super(UNSAFE_SSL_FACTORY, null, null);
    }

    /**
     * 创建指定超时的工厂 Bean。
     *
     * @param connectTimeout 连接超时（可为 {@code null}，零或负值不允许）
     * @param readTimeout    读取超时（可为 {@code null}，零或负值不允许）
     */
    public UnsafeJdkClientHttpRequestFactoryBean(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        super(UNSAFE_SSL_FACTORY, connectTimeout, readTimeout);
    }
}

package com.github.yingzhuo.bayonet.webcli.factory.apache5;

import com.github.yingzhuo.bayonet.webcli.util.SSLFactoryFactories;
import nl.altindag.ssl.SSLFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

/**
 * 信任所有证书的 {@link Apache5ClientHttpRequestFactoryBean}。
 *
 * <p>预配置为信任所有 SSL/TLS 证书（含自签名证书）并禁用主机名验证，
 * 适用于访问自签名或域名不匹配的 HTTPS 端点。</p>
 *
 * <p><b>安全警告：</b>此工厂创建的 {@link org.springframework.http.client.ClientHttpRequestFactory ClientHttpRequestFactory}
 * 会跳过 SSL 证书链校验和主机名验证，仅在开发、测试或连接受控内网时使用。生产环境应优先使用
 * {@link Apache5ClientHttpRequestFactoryBean} 并配置自定义信任策略。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * @Bean
 * public UnsafeApache5ClientHttpRequestFactoryBean clientHttpRequestFactory() {
 *     return new UnsafeApache5ClientHttpRequestFactoryBean();
 * }
 * }</pre>
 *
 * @author 应卓
 * @see Apache5ClientHttpRequestFactoryBean
 * @see SSLFactory
 * @since 4.1.0
 */
@ApiStatus.Experimental
@Deprecated
public final class UnsafeApache5ClientHttpRequestFactoryBean extends Apache5ClientHttpRequestFactoryBean {

    /**
     * 创建默认的工厂 Bean。
     * <p>连接超时默认 10 秒，读取超时默认 30 秒。</p>
     */
    public UnsafeApache5ClientHttpRequestFactoryBean() {
        super(SSLFactoryFactories.createUnsafe());
    }

    /**
     * 创建指定超时的工厂 Bean。
     *
     * @param connectTimeout 连接超时（可为 {@code null}，零或负值不允许）
     * @param readTimeout    读取超时（可为 {@code null}，零或负值不允许）
     */
    public UnsafeApache5ClientHttpRequestFactoryBean(@Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        super(SSLFactoryFactories.createUnsafe(), connectTimeout, readTimeout);
    }
}

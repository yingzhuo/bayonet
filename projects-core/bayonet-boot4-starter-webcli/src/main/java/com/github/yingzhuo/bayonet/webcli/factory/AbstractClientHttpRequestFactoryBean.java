package com.github.yingzhuo.bayonet.webcli.factory;

import com.github.yingzhuo.bayonet.webcli.util.SSLFactoryFactories;
import nl.altindag.ssl.SSLFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

import java.time.Duration;

import static java.util.Objects.requireNonNullElse;

/**
 * {@link ClientHttpRequestFactory} 工厂 Bean 的抽象基类。
 *
 * <p>封装了 SSL 配置校验、默认超时常量和通用的 {@link #getObjectType()} 实现。
 * 子类需实现 {@link #getObject()} 创建具体的 {@link ClientHttpRequestFactory} 实例，
 * 并按需重写 {@link #afterPropertiesSet()} 和 {@link #destroy()} 管理资源生命周期。</p>
 *
 * <p>框架提供两个内置子类体系：</p>
 * <ul>
 *   <li>{@code jdk11} 包 — 基于 JDK {@link java.net.http.HttpClient}</li>
 *   <li>{@code apache5} 包 — 基于 Apache HttpClient 5</li>
 * </ul>
 *
 * <p>SSL/TLS 配置基于 <a href="https://github.com/Hakky54/ayza">ayza</a> 开源库（{@link SSLFactory}），
 * 支持自定义信任材料、自签名证书、客户端证书认证等场景。</p>
 *
 * @author 应卓
 * @see #getObject()
 * @see #afterPropertiesSet()
 * @see #destroy()
 * @since 4.1.0
 */
public abstract class AbstractClientHttpRequestFactoryBean
        implements FactoryBean<ClientHttpRequestFactory>, InitializingBean, DisposableBean {
    protected static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    protected static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);

    protected final SSLFactory sslFactory;
    protected final Duration connectTimeout;
    protected final Duration readTimeout;

    protected AbstractClientHttpRequestFactoryBean() {
        this(SSLFactoryFactories.createDefault());
    }

    protected AbstractClientHttpRequestFactoryBean(SSLFactory sslFactory) {
        this(sslFactory, null, null);
    }

    protected AbstractClientHttpRequestFactoryBean(SSLFactory sslFactory, @Nullable Duration connectTimeout, @Nullable Duration readTimeout) {
        Assert.notNull(sslFactory, "SSLFactory must not be null");

        if (connectTimeout != null) {
            Assert.isTrue(!connectTimeout.isZero() && !connectTimeout.isNegative(),
                    "connect timeout must be positive, but got " + connectTimeout);
        }
        if (readTimeout != null) {
            Assert.isTrue(!readTimeout.isZero() && !readTimeout.isNegative(),
                    "read timeout must be positive, but got " + readTimeout);
        }

        this.sslFactory = sslFactory;
        this.connectTimeout = requireNonNullElse(connectTimeout, DEFAULT_CONNECT_TIMEOUT);
        this.readTimeout = requireNonNullElse(readTimeout, DEFAULT_READ_TIMEOUT);
    }

    @Override
    public final Class<?> getObjectType() {
        return ClientHttpRequestFactory.class;
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public void destroy() {
    }
}

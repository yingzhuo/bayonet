package com.github.yingzhuo.bayonet.webcli.factory;

import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import com.github.yingzhuo.bayonet.webcli.support.TrustAllTrustManager;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Optional;

/**
 * JDK {@link java.net.http.HttpClient} 的 {@link ClientHttpRequestFactory} 工厂 Bean 抽象基类。
 *
 * <p>提供了 SSL/TLS 配置的统一管理，包括信任库加载、客户端证书加载和主机名验证控制。
 * 子类只需关注具体的超时配置和工厂实例的组装。</p>
 *
 * <p><b>功能特性</b></p>
 * <ul>
 *   <li>信任库加载 — 支持从资源路径（classpath 或文件系统）加载自定义信任库，
 *       也支持信任所有自签名证书</li>
 *   <li>客户端证书认证（mTLS）— 支持双向 SSL 认证，从资源路径加载客户端密钥库</li>
 *   <li>主机名验证控制 — 开启信任所有证书时自动禁用主机名验证以支持域名不匹配的场景</li>
 *   <li>Spring 资源加载 — 信任库和密钥库路径支持 Spring 资源协议（classpath:、file: 等）和占位符替换</li>
 * </ul>
 *
 * <p><b>信任材料优先级</b></p>
 * <ol>
 *   <li>若设置了 {@link #trustStoreLocation}，则从指定资源路径加载自定义信任库</li>
 *   <li>否则若 {@link #trustAllIfNoTrustStore} 为 {@code true}，则信任所有证书（含自签名证书）</li>
 *   <li>否则使用 JDK 默认信任库（{@code cacerts}）</li>
 * </ol>
 *
 * @author 应卓
 * @see JdkClientHttpRequestFactoryBean
 * @see TrustAllTrustManager
 * @since 4.1.0
 */
public abstract class AbstractClientHttpRequestFactoryBean implements
        FactoryBean<ClientHttpRequestFactory>, InitializingBean,
        ResourceLoaderAware, EnvironmentAware {

    /**
     * 资源加载器，用于从 classpath 或文件系统加载信任库和密钥库文件。
     * <p>通过 {@link ResourceLoaderAware} 由 Spring 容器注入。</p>
     */
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    /**
     * Spring 环境，用于解析资源路径中的占位符（如 {@code ${truststore.path}}）。
     * <p>通过 {@link EnvironmentAware} 由 Spring 容器注入。</p>
     */
    private Environment environment = new StandardEnvironment();

    /**
     * 自定义信任库的资源路径。
     * <p>支持 Spring 资源协议：{@code classpath:}、{@code file:} 等，
     * 也支持占位符替换（如 {@code ${custom.truststore.location}}）。</p>
     * <p>设置此字段后，{@link #trustAllIfNoTrustStore} 对证书链校验无影响。</p>
     */
    private @Nullable String trustStoreLocation;

    /**
     * 自定义信任库的密码。
     * <p>当 {@link #trustStoreLocation} 已设置时此字段必填。</p>
     */
    private @Nullable char[] trustStorePassword;

    /**
     * 自定义信任库的类型。
     * <p>默认值为 {@link KeyStoreType#PKCS12}。</p>
     */
    private @Nullable KeyStoreType trustStoreType = KeyStoreType.PKCS12;

    /**
     * 当未设置自定义信任库时，是否信任所有证书。
     * <p>为 {@code true} 时，除信任所有证书外还会自动禁用主机名验证，
     * 以支持访问自签名且域名不匹配的 HTTPS 端点。</p>
     */
    private @Setter boolean trustAllIfNoTrustStore = false;

    /**
     * 客户端证书密钥库的资源路径（用于 mTLS 双向认证）。
     * <p>支持 Spring 资源协议：{@code classpath:}、{@code file:} 等，
     * 也支持占位符替换。</p>
     * <p>当此字段已设置时，{@link #clientStorePassword} 必填。</p>
     */
    private @Nullable String clientStoreLocation;

    /**
     * 客户端证书密钥库的密码。
     * <p>当 {@link #clientStoreLocation} 已设置时此字段必填。</p>
     */
    private @Nullable char[] clientStorePassword;

    /**
     * 客户端证书密钥库的类型。
     * <p>默认值为 {@link KeyStoreType#PKCS12}。</p>
     */
    private @Nullable KeyStoreType clientStoreType = KeyStoreType.PKCS12;

    @Override
    public final Class<?> getObjectType() {
        return ClientHttpRequestFactory.class;
    }

    @Override
    public final void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    @Override
    public final void setEnvironment(Environment environment) {
        Assert.notNull(environment, "environment must not be null");
        this.environment = environment;
    }

    /**
     * 设置自定义信任库的资源路径。
     *
     * @param trustStoreLocation 自定义信任库资源路径，不能为空
     */
    public final void setTrustStoreLocation(String trustStoreLocation) {
        Assert.hasText(trustStoreLocation, "trustStoreLocation must not be empty");
        this.trustStoreLocation = trustStoreLocation;
    }

    /**
     * 设置自定义信任库的密码。
     *
     * @param trustStorePassword 自定义信任库密码，不能为空
     */
    public final void setTrustStorePassword(String trustStorePassword) {
        Assert.hasText(trustStorePassword, "trustStorePassword must not be empty");
        this.trustStorePassword = trustStorePassword.toCharArray();
    }

    /**
     * 设置自定义信任库的类型。
     *
     * @param trustStoreType 信任库类型，不能为 {@code null}
     */
    public final void setTrustStoreType(KeyStoreType trustStoreType) {
        Assert.notNull(trustStoreType, "trustStoreType must not be null");
        this.trustStoreType = trustStoreType;
    }

    /**
     * 设置客户端证书密钥库的资源路径（用于 mTLS 双向认证）。
     *
     * @param clientStoreLocation 客户端证书密钥库资源路径，不能为空
     */
    public void setClientStoreLocation(String clientStoreLocation) {
        Assert.hasText(clientStoreLocation, "clientStoreLocation must not be empty");
        this.clientStoreLocation = clientStoreLocation;
    }

    /**
     * 设置客户端证书密钥库的密码。
     *
     * @param clientStorePassword 客户端证书密钥库密码，不能为空
     */
    public void setClientStorePassword(String clientStorePassword) {
        Assert.hasText(clientStorePassword, "clientStorePassword must not be empty");
        this.clientStorePassword = clientStorePassword.toCharArray();
    }

    /**
     * 设置客户端证书密钥库的类型。
     *
     * @param clientStoreType 密钥库类型，不能为 {@code null}
     */
    public void setClientStoreType(KeyStoreType clientStoreType) {
        Assert.notNull(clientStoreType, "clientStoreType must not be null");
        this.clientStoreType = clientStoreType;
    }

    /**
     * 从资源路径加载密钥库文件。
     * <p>支持 Spring 资源协议和占位符替换。使用 {@link ResourceLoader#getResource(String)}
     * 加载资源，然后调用 {@link KeyStore#load(java.io.InputStream, char[])} 解析密钥库。</p>
     *
     * @param type     密钥库类型（如 {@code PKCS12}、{@code JKS}）
     * @param location 密钥库资源路径（支持 Spring 占位符）
     * @param pwd      密钥库密码（可为 {@code null}）
     * @return 加载好的 {@link KeyStore} 实例
     * @throws KeyStoreException        密钥库操作异常
     * @throws IOException              读取资源时异常
     * @throws CertificateException     证书解析异常
     * @throws NoSuchAlgorithmException  密码算法不支持
     */
    protected final KeyStore loadKeyStore(String type, String location, char[] pwd) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        location = environment.resolvePlaceholders(location);

        try (var stream = resourceLoader.getResource(location).getInputStream()) {
            var ks = KeyStore.getInstance(type);
            ks.load(stream, pwd);
            return ks;
        }
    }

    /**
     * 初始化 Bean 时校验配置一致性。
     *
     * <p>校验规则：</p>
     * <ul>
     *   <li>若设置了 {@link #trustStoreLocation}，则 {@link #trustStorePassword} 不能为空</li>
     *   <li>若设置了 {@link #trustStorePassword}，则 {@link #trustStoreLocation} 不能为空</li>
     *   <li>若设置了 {@link #clientStoreLocation}，则 {@link #clientStorePassword} 不能为空</li>
     *   <li>若设置了 {@link #clientStorePassword}，则 {@link #clientStoreLocation} 不能为空</li>
     * </ul>
     *
     * @throws IllegalArgumentException 配置不一致时抛出
     */
    @Override
    public void afterPropertiesSet() {
        if (this.trustStoreLocation != null) {
            Assert.notNull(this.trustStorePassword, "trustStorePassword must not be null when trustStoreLocation is set");
        }
        if (this.trustStoreLocation == null && this.trustStorePassword != null) {
            throw new IllegalArgumentException("trustStoreLocation must not be null when trustStorePassword is set");
        }

        if (this.clientStoreLocation != null) {
            Assert.notNull(this.clientStorePassword, "clientStorePassword must not be null when clientStoreLocation is set");
        }
        if (this.clientStoreLocation == null && this.clientStorePassword != null) {
            throw new IllegalArgumentException("clientStoreLocation must not be null when clientStorePassword is set");
        }
    }

    /**
     * 创建并配置 SSL 上下文。
     *
     * <p>按以下优先级构建信任材料：</p>
     * <ol>
     *   <li>若 {@link #trustStoreLocation} 已设置，则从指定路径加载自定义信任库进行证书校验</li>
     *   <li>否则若 {@link #trustAllIfNoTrustStore} 为 {@code true}，则信任所有证书（含自签名证书）</li>
     *   <li>否则不设置信任管理器（{@code null}），使用 JDK 默认信任库（{@code cacerts}）</li>
     * </ol>
     *
     * <p>若 {@link #clientStoreLocation} 已设置，同时加载客户端证书（用于 mTLS 双向认证）。</p>
     *
     * @return 配置好的 {@link SSLContext} 实例
     * @throws NoSuchAlgorithmException   TLS 算法不支持
     * @throws CertificateException       证书解析异常
     * @throws KeyStoreException          密钥库操作异常
     * @throws IOException                读取资源异常
     * @throws UnrecoverableKeyException  密钥不可恢复（密码错误等）
     * @throws KeyManagementException     SSL 上下文初始化异常
     */
    protected final SSLContext createSSLContext() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        var ctx = SSLContext.getInstance("TLS");

        // CLIENT-SIDE Key
        KeyManager[] keyManagers = null;
        if (this.clientStoreLocation != null) {
            var clientKeyStore = loadKeyStore(this.clientStoreType.name(), this.clientStoreLocation, this.clientStorePassword);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKeyStore, this.clientStorePassword);
            keyManagers = kmf.getKeyManagers();
        }

        // TRUST
        TrustManager[] trustManagers = null;
        if (this.trustStoreLocation != null) {
            var trustStore = loadKeyStore(this.trustStoreType.name(), this.trustStoreLocation, this.trustStorePassword);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            trustManagers = tmf.getTrustManagers();
        } else if (this.trustAllIfNoTrustStore) {
            trustManagers = new TrustManager[]{new TrustAllTrustManager()};
        }

        ctx.init(keyManagers, trustManagers, new java.security.SecureRandom());
        return ctx;
    }

    /**
     * 获取 SSL 参数配置，按需禁用主机名验证。
     *
     * <p>当 {@link #trustAllIfNoTrustStore} 为 {@code true} 且未设置 {@link #trustStoreLocation} 时，
     * 返回包含禁用主机名验证的 {@link SSLParameters}，以支持访问自签名且域名不匹配的 HTTPS 端点。
     * 否则返回 {@link Optional#empty()}，使用 JDK 默认主机名验证行为。</p>
     *
     * @return 包含禁用主机名验证的 {@link SSLParameters}，或空
     * @throws NoSuchAlgorithmException 获取默认 SSL 参数时异常
     */
    protected final Optional<SSLParameters> createSSLParametersIfNecessary() throws NoSuchAlgorithmException {
        if (trustAllIfNoTrustStore && trustStoreLocation == null) {
            var sslParams = SSLContext.getDefault().getDefaultSSLParameters();
            sslParams.setEndpointIdentificationAlgorithm(null);
            return Optional.of(sslParams);
        } else {
            return Optional.empty();
        }
    }

}

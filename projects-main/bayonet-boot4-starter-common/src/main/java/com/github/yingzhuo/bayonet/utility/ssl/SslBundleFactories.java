package com.github.yingzhuo.bayonet.utility.ssl;

import com.github.yingzhuo.bayonet.utility.net.SSLContextFactories;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslManagerBundle;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * {@link SslBundle} 工厂类。
 *
 * <p>提供创建默认 SSL 捆绑和不安全 SSL 捆绑的静态便捷方法。
 * 可用于配置 HTTP 连接器的 SSL/TLS 设置。</p>
 *
 * @author 应卓
 * @see SslBundle
 * @see SSLContextFactories
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SslBundleFactories {

    /**
     * 获取系统默认的 {@link SslBundle}。
     *
     * @return 系统默认 {@link SslBundle}（非 {@code null}）
     */
    public static SslBundle createDefault() {
        return SslBundle.systemDefault();
    }

    /**
     * 创建一个信任所有证书的 {@link SslBundle}。
     * <p>内部使用 {@link SSLContextFactories#createInsecure()} 跳过服务端证书校验，
     * 仅建议在开发或测试环境中使用。</p>
     *
     * @return 不安全的 {@link SslBundle}（非 {@code null}）
     * @throws IllegalStateException 初始化失败时抛出
     */
    public static SslBundle createInsecure() {
        try {
            var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(null, null);

            var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);

            var sslCtx = SSLContextFactories.createInsecure();
            return SslBundle.of(null, null, null, "TLS", new SslManagerBundle() {
                @Override
                public KeyManagerFactory getKeyManagerFactory() {
                    return kmf;
                }

                @Override
                public TrustManagerFactory getTrustManagerFactory() {
                    return tmf;
                }

                @Override
                public SSLContext createSslContext(String protocol) {
                    return sslCtx;
                }
            });
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException ex) {
            throw new IllegalStateException("Could not initialize system insecure SslBundle: " + ex.getMessage(), ex);
        }
    }
}

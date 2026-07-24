package com.github.yingzhuo.bayonet.utility.net;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * 信任所有证书的 {@link X509TrustManager} 实现。
 *
 * <p>所有 {@code checkXxxTrusted} 方法均为空实现，即无条件信任任何服务端或客户端证书。
 * <b>仅建议在开发或测试环境中使用，生产环境存在安全风险。</b></p>
 *
 * <p>使用 {@link #getInstance()} 获取单例。</p>
 *
 * @author 应卓
 * @see SSLContextFactories
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InsecureX509TrustManager implements X509TrustManager {

    /**
     * 获取 {@link InsecureX509TrustManager} 单例实例。
     *
     * @return 单例实例
     */
    public static InsecureX509TrustManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        // 无条件信任
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        // 无条件信任
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    // ------

    private static final class LazyHolder {
        private static final InsecureX509TrustManager INSTANCE = new InsecureX509TrustManager();
    }
}

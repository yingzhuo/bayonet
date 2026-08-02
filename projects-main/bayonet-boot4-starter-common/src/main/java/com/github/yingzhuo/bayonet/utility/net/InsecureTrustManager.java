package com.github.yingzhuo.bayonet.utility.net;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * 信任所有证书的 {@link X509TrustManager} 实现。
 *
 * <p>所有 {@code checkXxxTrusted} 方法均为空实现，即无条件信任任何服务端或客户端证书。
 * 仅建议在开发或测试环境中使用，生产环境存在安全风险。</p>
 *
 * @author 应卓
 * @see SSLFactories
 * @since 4.1.1
 */
public final class InsecureTrustManager extends X509ExtendedTrustManager implements X509TrustManager, TrustManager {

    /**
     * 私有构造器
     */
    private InsecureTrustManager() {
    }

    /**
     * 获取 {@link InsecureTrustManager} 单例实例。
     *
     * @return 单例实例
     */
    public static InsecureTrustManager getSingleton() {
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

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        // 无条件信任
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        // 无条件信任
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
        // 无条件信任
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
        // 无条件信任
    }

    @Override
    public String toString() {
        return "InsecureTrustManager (Trust All)";
    }

    // ------

    private static final class LazyHolder {
        private static final InsecureTrustManager INSTANCE = new InsecureTrustManager();
    }

}

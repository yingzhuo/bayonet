package com.github.yingzhuo.bayonet.webcli.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnsafeTrustAllTrustManager implements X509TrustManager {

    public static UnsafeTrustAllTrustManager getSingletonInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    // ------

    private static class LazyHolder {
        private static final UnsafeTrustAllTrustManager INSTANCE = new UnsafeTrustAllTrustManager();
    }

}

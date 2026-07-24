package com.github.yingzhuo.bayonet.utility.net;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * {@link SSLContext} 工厂类。
 *
 * <p>提供创建不安全（信任所有证书）和默认 SSL 上下文的静态便捷方法。</p>
 *
 * @author 应卓
 * @see InsecureX509TrustManager
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SSLContextFactories {

    /**
     * 创建一个信任所有证书的 {@link SSLContext}。
     * <p>内部使用 {@link InsecureX509TrustManager} 跳过服务端证书校验，
     * 仅建议在开发或测试环境中使用。</p>
     *
     * @return 不安全的 {@link SSLContext}（非 {@code null}）
     * @throws IllegalArgumentException 创建失败时抛出
     */
    public static SSLContext createInsecure() {
        try {
            var ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{InsecureX509TrustManager.getInstance()}, new SecureRandom());
            return ctx;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 获取默认的 {@link SSLContext}。
     *
     * @return 默认 {@link SSLContext}（非 {@code null}）
     * @throws IllegalArgumentException 获取失败时抛出
     */
    public static SSLContext createDefault() {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}

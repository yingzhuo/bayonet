package com.github.yingzhuo.bayonet.utility.net;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

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
public final class SSLFactories {

    /**
     * 创建一个信任所有证书的 {@link SSLContext} 和 跳过 hostname 检查的 {@link SSLParameters}。
     * <p>内部使用 {@link InsecureX509TrustManager} 跳过服务端证书校验，
     * 仅建议在开发或测试环境中使用。</p>
     *
     * @return 不安全的 {@link SSLContext}（非 {@code null}）
     * @throws IllegalArgumentException 创建失败时抛出
     */
    public static ContextAndParameters createInsecure() {
        try {
            var ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{InsecureX509TrustManager.getSingletonInstance()}, new java.security.SecureRandom());

            var params = new SSLParameters();
            params.setEndpointIdentificationAlgorithm(null);

            return new ContextAndParameters(ctx, params);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 获取默认的 {@link SSLContext} 和 {@link SSLParameters}
     *
     * @return 默认 {@link SSLContext}（非 {@code null}）
     * @throws IllegalArgumentException 获取失败时抛出
     */
    public static ContextAndParameters createDefault() {
        try {
            return new ContextAndParameters(
                    SSLContext.getDefault(),
                    new SSLParameters()
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}

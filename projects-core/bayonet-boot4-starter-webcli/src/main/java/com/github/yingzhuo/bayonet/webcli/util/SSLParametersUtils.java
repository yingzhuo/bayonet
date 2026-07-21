package com.github.yingzhuo.bayonet.webcli.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.security.NoSuchAlgorithmException;

/**
 * {@link SSLParameters} 的工具类。
 *
 * <p>提供便捷方法快速获取默认或禁用主机名验证的 SSL 参数。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SSLParametersUtils {

    /**
     * 获取默认的 {@link SSLParameters}。
     *
     * <p>返回 JVM 当前默认 SSL 上下文的标准参数配置。</p>
     *
     * @return 默认 SSLParameters 实例（非 {@code null}）
     * @throws IllegalStateException 获取默认 SSL 上下文失败时抛出
     */
    public static SSLParameters createDefault() {
        try {
            return SSLContext.getDefault().getDefaultSSLParameters();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to get default SSLParameters", e);
        }
    }

    /**
     * 创建禁用主机名验证的 {@link SSLParameters}。
     *
     * <p>返回的 SSL 参数禁用了端点标识算法（{@code setEndpointIdentificationAlgorithm(null)}），
     * 即不验证服务器主机名与证书域名是否匹配。适用于访问自签名证书或域名不匹配的 HTTPS 端点。</p>
     *
     * <p><b>安全警告：</b>此方法返回的参数会跳过主机名验证，仅在需要连接不受信任的端点时使用。
     * 生产环境应优先使用 {@link #createDefault()} 或配置自定义信任策略。</p>
     *
     * @return 禁用主机名验证的 SSLParameters 实例（非 {@code null}）
     * @throws IllegalStateException 获取默认 SSL 上下文失败时抛出
     */
    public static SSLParameters createUnsafe() {
        var unsafe = createDefault();
        unsafe.setEndpointIdentificationAlgorithm(null);
        return unsafe;
    }

}

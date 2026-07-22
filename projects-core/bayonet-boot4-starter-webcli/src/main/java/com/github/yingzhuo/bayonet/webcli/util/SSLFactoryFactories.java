package com.github.yingzhuo.bayonet.webcli.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.altindag.ssl.SSLFactory;

/**
 * {@link SSLFactory} 便捷工厂工具类。
 *
 * <p>提供预配置的 {@link SSLFactory} 创建方法，封装了默认信任材料和 unsafe 两种常用配置。</p>
 *
 * @author 应卓
 * @see SSLFactory
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SSLFactoryFactories {

    /**
     * 创建信任所有证书的 {@link SSLFactory}。
     *
     * <p>跳过 SSL 证书链校验并禁用主机名验证，适用于访问自签名证书或域名不匹配的 HTTPS 端点。</p>
     *
     * <p><b>安全警告：</b>仅在开发、测试或连接受控内网时使用。</p>
     *
     * @return {@link SSLFactory} 实例
     */
    public static SSLFactory createUnsafe() {
        // @formatter:off
        return SSLFactory.builder()
                .withUnsafeTrustMaterial()
                .withUnsafeHostnameVerifier()
                .build();
        // @formatter:on
    }

    /**
     * 创建使用 JDK 默认信任材料的 {@link SSLFactory}。
     *
     * <p>使用系统默认信任库，验证服务端证书的合法性，适用于生产环境。</p>
     *
     * @return {@link SSLFactory} 实例
     */
    public static SSLFactory createDefault() {
        // @formatter:off
        return SSLFactory.builder()
                .withDefaultTrustMaterial()
                .build();
        // @formatter:on
    }
}

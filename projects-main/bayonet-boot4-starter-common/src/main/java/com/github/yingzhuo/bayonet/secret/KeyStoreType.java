package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * 支持的 KeyStore 类型。
 *
 * <ul>
 *   <li>{@link #PKCS12} — PKCS#12 格式（.p12 / .pfx）</li>
 *   <li>{@link #JKS} — Java KeyStore 格式（.jks）</li>
 *   <li>{@link #BCFKS} — Bouncy Castle KeyStore 格式（由 BouncyCastleProvider 提供）</li>
 * </ul>
 *
 * @author 应卓
 * @see KeyStoreTypeConverter
 * @since 4.1.0
 */
public enum KeyStoreType {

    /**
     * PKCS#12
     */
    PKCS12,

    /**
     * JKS
     */
    JKS,

    /**
     * BCFKS（Bouncy Castle KeyStore）。
     * <p>由 {@code BouncyCastleProvider}（"BC"）提供支持。</p>
     *
     * @since 4.1.1
     */
    BCFKS;

    /**
     * 获取默认 KeyStore 类型。
     *
     * @return {@link #PKCS12}
     */
    public static KeyStoreType getDefault() {
        return PKCS12;
    }

    /**
     * 将字符串解析为 {@link KeyStoreType}。
     * <p>支持的字符串值（不区分大小写）：</p>
     * <ul>
     *   <li>{@code pkcs12}、{@code pkcs#12}、{@code pfx}、{@code p12} → {@link #PKCS12}</li>
     *   <li>{@code jks} → {@link #JKS}</li>
     *   <li>{@code bcfks} → {@link #BCFKS}</li>
     * </ul>
     *
     * @param type 字符串类型，为 {@code null} 时返回默认值 {@link #PKCS12}
     * @return 匹配的 {@link KeyStoreType}
     * @throws IllegalArgumentException 无法识别的类型字符串
     * @see KeyStoreTypeConverter
     * @since 4.1.1
     */
    public static KeyStoreType toKeyStore(@Nullable String type) {
        if (type == null) {
            return getDefault();
        }

        return switch (type.toLowerCase(Locale.ROOT)) {
            case "pkcs12", "pkcs#12", "pfx", "p12" -> PKCS12;
            case "jks" -> JKS;
            case "bcfks" -> BCFKS;
            default -> throw new IllegalArgumentException("unknown keystore type: '" + type + "'");
        };
    }
}

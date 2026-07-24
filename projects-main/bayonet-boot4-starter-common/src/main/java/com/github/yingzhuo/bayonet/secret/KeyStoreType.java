package com.github.yingzhuo.bayonet.secret;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * 支持的 KeyStore 类型。
 *
 * <ul>
 *   <li>{@link #PKCS12} — PKCS#12 格式（.p12 / .pfx）</li>
 *   <li>{@link #JKS} — Java KeyStore 格式（.jks）</li>
 * </ul>
 *
 * @author 应卓
 * @since 4.1.0
 */
@RequiredArgsConstructor
public enum KeyStoreType {

    /**
     * PKCS#12
     */
    PKCS12("pkcs12"),

    /**
     * JKS
     */
    JKS("jks");

    /**
     * KeyStore 类型名称（小写），用于底层 API 调用。
     */
    private final String name;

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
     * </ul>
     *
     * @param type 字符串类型，为 {@code null} 时返回默认值 {@link #PKCS12}
     * @return 匹配的 {@link KeyStoreType}
     * @throws IllegalArgumentException 无法识别的类型字符串
     */
    public static KeyStoreType toKeyStore(@Nullable String type) {
        if (type == null) {
            return getDefault();
        }

        if (type.equalsIgnoreCase("pkcs12") ||
                type.equalsIgnoreCase("pkcs#12") ||
                type.equalsIgnoreCase("pfx") ||
                type.equalsIgnoreCase("p12")
        ) {
            return PKCS12;
        } else if (type.equalsIgnoreCase("jks")) {
            return JKS;
        }

        throw new IllegalArgumentException("Unsupported keystore type: '" + type + "'");
    }

}

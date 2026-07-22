package com.github.yingzhuo.bayonet.secret;

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
public enum KeyStoreType {
    PKCS12, JKS
}

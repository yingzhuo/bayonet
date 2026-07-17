package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 密钥与证书的聚合容器。
 * <p>持有私钥、公钥（从证书中提取）及证书链，适用于 TLS/mTLS 等需要同时使用密钥和证书的场景。</p>
 */
public interface KeyBundle {

    /**
     * 获取公钥。
     *
     * @param <T> 公钥类型
     * @return 公钥（非 {@code null}）
     */
    <T extends PublicKey> T getPublicKey();

    /**
     * 获取私钥。
     *
     * @param <T> 私钥类型
     * @return 私钥（非 {@code null}）
     */
    <T extends PrivateKey> T getPrivateKey();

    /**
     * 获取 {@link KeyPair}。
     *
     * @return {@link KeyPair}（非 {@code null}）
     */
    default KeyPair getKeyPair() {
        return new KeyPair(getPublicKey(), getPrivateKey());
    }

    /**
     * 获取证书链中的第一个证书（终端实体证书）。
     *
     * @param <T> 证书类型
     * @return 终端实体证书（非 {@code null}）
     */
    <T extends X509Certificate> T getCertificate();

    /**
     * 获取完整证书链（不可变）。
     *
     * @return 证书链（非 {@code null}，可能包含多个中间 CA 证书）
     */
    List<X509Certificate> getCertificateChain();

    /**
     * 获取资源路径
     *
     * @return 资源路径
     */
    @Nullable
    default String getLocation() {
        return null;
    }

}

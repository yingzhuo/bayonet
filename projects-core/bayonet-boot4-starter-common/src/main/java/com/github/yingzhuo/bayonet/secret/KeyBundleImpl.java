package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * {@link KeyBundle} 的默认实现。
 * <p>通过证书链和私钥构造，公钥从终端实体证书中提取。
 * 证书链在构造时进行防御性拷贝，getter 返回不可变视图。</p>
 */
public class KeyBundleImpl implements KeyBundle {

    private final List<X509Certificate> certificateChain;
    private final PrivateKey privateKey;
    private final @Nullable String location;

    /**
     * 构造器。
     *
     * @param certificateChain 证书链（非 {@code null}、非空、不含 null 元素）
     * @param privateKey       私钥（非 {@code null}）
     * @throws IllegalArgumentException 若参数不满足约束
     */
    public KeyBundleImpl(List<X509Certificate> certificateChain, PrivateKey privateKey, @Nullable String location) {
        Assert.notNull(certificateChain, "certificateChain must not be null");
        Assert.notEmpty(certificateChain, "certificateChain must not be empty");
        Assert.noNullElements(certificateChain, "certificateChain must not contain null elements");
        Assert.notNull(privateKey, "privateKey must not be null");

        this.certificateChain = List.copyOf(certificateChain);
        this.privateKey = privateKey;
        this.location = location;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PublicKey> T getPublicKey() {
        return (T) getCertificate().getPublicKey();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PrivateKey> T getPrivateKey() {
        return (T) this.privateKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends X509Certificate> T getCertificate() {
        return (T) this.certificateChain.get(0);
    }

    @Override
    public List<X509Certificate> getCertificateChain() {
        return certificateChain;
    }

    @Override
    public @Nullable String getLocation() {
        return location;
    }
}

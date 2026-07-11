package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 将 PEM 内容包装为 Spring {@link org.springframework.core.io.Resource}。
 * <p>使用 Spring Boot 的 {@link PemContent} 加载 PEM 格式的证书和私钥。</p>
 */
public class PemResource extends SecretResource<PemContent> {

    private final PemContent pemContent;

    /**
     * 从输入流加载 PEM 内容。
     *
     * @param stream PEM 文件输入流
     */
    public PemResource(InputStream stream) {
        Assert.notNull(stream, "stream must not be null");

        try {
            this.pemContent = PemContent.load(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // NoOp
            }
        }
    }

    @Override
    public String getDescription() {
        return "Pem [" + this.pemContent + "]";
    }

    @Override
    public PemContent getSecret() {
        return this.pemContent;
    }

    /**
     * 获取所有 X.509 证书。
     *
     * @return 证书列表，无证书时返回空列表
     */
    public List<X509Certificate> getCertificates() {
        try {
            return pemContent.getCertificates();
        } catch (IllegalStateException e) {
            return List.of();
        }
    }

    /**
     * 获取第一张 X.509 证书。
     *
     * @return 证书，不存在时返回 {@code null}
     */
    @Nullable
    public X509Certificate getCertificate() {
        var cs = getCertificates();
        return cs.isEmpty() ? null : cs.get(0);
    }

    /**
     * 获取私钥。
     *
     * @param keypass 私钥密码，没有密码则为 {@code null}
     * @param <T>     私钥类型
     * @return 私钥，不存在时返回 {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PrivateKey> T getPrivateKey(@Nullable String keypass) {
        try {
            if (keypass == null) {
                return (T) pemContent.getPrivateKey();
            } else {
                return (T) pemContent.getPrivateKey(keypass);
            }
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * 获取公钥。
     *
     * @param <T> 公钥类型
     * @return 公钥，不存在时返回 {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PublicKey> T getPublicKey() {
        var cert = getCertificate();
        if (cert == null) return null;
        return (T) cert.getPublicKey();
    }

    /**
     * 获取密钥对。
     *
     * @param keypass 私钥密码，没有密码则为 {@code null}
     * @return 密钥对，公钥或私钥缺失时返回 {@code null}
     */
    @Nullable
    public KeyPair getKeyPair(@Nullable String keypass) {
        var publicKey = getPublicKey();
        var privateKey = getPrivateKey(keypass);
        if (publicKey == null || privateKey == null) return null;
        return new KeyPair(publicKey, privateKey);
    }

}

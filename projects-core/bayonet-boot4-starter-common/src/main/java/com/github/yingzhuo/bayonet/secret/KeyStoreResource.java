package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.*;
import java.security.cert.Certificate;

/**
 * 将 {@link KeyStore} 包装为 Spring {@link org.springframework.core.io.Resource}。
 * <p>提供便捷方法获取证书链、密钥对及公钥/私钥。</p>
 */
public class KeyStoreResource extends SecretResource<KeyStore> {

    private final KeyStore keyStore;

    /**
     * 从输入流加载密钥库。
     *
     * @param type      密钥库类型（PKCS12 / JKS）
     * @param stream    密钥库文件输入流
     * @param storepass 密钥库密码
     */
    public KeyStoreResource(KeyStoreType type, InputStream stream, String storepass) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(stream, "stream must not be null");
        Assert.notNull(storepass, "storepass must not be null");

        try {
            this.keyStore = KeyStore.getInstance(type.name());
            this.keyStore.load(stream, storepass.toCharArray());
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
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
        return "KeyStore [" + this.keyStore + "]";
    }

    @Override
    public KeyStore getSecret() {
        return this.keyStore;
    }

    /**
     * 获取指定别名的证书链。
     *
     * @param alias 证书别名
     * @return 证书链数组，无匹配时返回空数组
     */
    public Certificate[] getCertificateChain(String alias) {
        try {
            var certs = keyStore.getCertificateChain(alias);
            if (certs == null) return new Certificate[0];
            return certs;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 获取指定别名的证书。
     *
     * @param alias 证书别名
     * @return 证书，未找到时返回 {@code null}
     */
    @Nullable
    public Certificate getCertificate(String alias) {
        try {
            return keyStore.getCertificate(alias);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 获取指定别名的密钥。
     *
     * @param alias   密钥别名
     * @param keypass 密钥密码，为 {@code null} 时视为空字符串
     * @param <T>     密钥类型
     * @return 密钥，未找到时返回 {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Key> T getKey(String alias, @Nullable String keypass) {
        keypass = keypass != null ? keypass : "";
        try {
            return (T) keyStore.getKey(alias, keypass.toCharArray());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 获取指定别名的密钥（默认密码为空字符串）。
     *
     * @param alias 密钥别名
     * @param <T>   密钥类型
     * @return 密钥，未找到时返回 {@code null}
     */
    @Nullable
    public <T extends Key> T getKey(String alias) {
        return getKey(alias, null);
    }

    /**
     * 获取指定别名的私钥。
     *
     * @param alias   密钥别名
     * @param keypass 密钥密码，为 {@code null} 时视为空字符串
     * @param <T>     私钥类型
     * @return 私钥，未找到时返回 {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PrivateKey> T getPrivateKey(String alias, @Nullable String keypass) {
        keypass = keypass != null ? keypass : "";
        try {
            return (T) keyStore.getKey(alias, keypass.toCharArray());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 获取指定别名的私钥（默认密码为空字符串）。
     *
     * @param alias 密钥别名
     * @param <T>   私钥类型
     * @return 私钥，未找到时返回 {@code null}
     */
    @Nullable
    public <T extends PrivateKey> T getPrivateKey(String alias) {
        return getPrivateKey(alias, null);
    }

    /**
     * 获取指定别名的公钥。
     *
     * @param alias 证书别名
     * @param <T>   公钥类型
     * @return 公钥，未找到时返回 {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PublicKey> T getPublicKey(String alias) {
        var cert = getCertificate(alias);
        if (cert == null) return null;
        return (T) cert.getPublicKey();
    }

    /**
     * 获取指定别名的密钥对。
     *
     * @param alias   证书/密钥别名
     * @param keypass 私钥密码，为 {@code null} 时视为空字符串
     * @return 密钥对，公钥或私钥缺失时返回 {@code null}
     */
    @Nullable
    public KeyPair getKeyPair(String alias, @Nullable String keypass) {
        PublicKey publicKey = getPublicKey(alias);
        PrivateKey privateKey = getPrivateKey(alias, keypass);
        if (publicKey == null || privateKey == null) return null;
        return new KeyPair(publicKey, privateKey);
    }

}

package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * {@link SecretBox} 基于 {@link KeyStore} 的默认实现。
 *
 * @author 应卓
 * @see SecretBox
 * @see KeyStoreUtils
 * @since 4.1.1
 */
public class KeyStoreSecretBox implements SecretBox {

    private final Resource resource;
    private final StoreType type;
    private final String storepass;
    private final Map<String, String> aliasToKeypass;
    private volatile @Nullable KeyStore keyStore;

    /**
     * 构造实例，不指定别名到密钥密码的映射。
     * <p>所有别名的密钥密码回退为存储密码。</p>
     *
     * @param resource  KeyStore 资源（非 {@code null}）
     * @param type      KeyStore 类型，为 {@code null} 时使用默认类型
     * @param storepass 存储密码（非空）
     */
    public KeyStoreSecretBox(Resource resource, @Nullable StoreType type, String storepass) {
        this(resource, type, storepass, null);
    }

    /**
     * 构造实例。
     * <p>未在映射中的别名，其密钥密码回退为存储密码。</p>
     *
     * @param resource       KeyStore 资源（非 {@code null}）
     * @param type           KeyStore 类型，为 {@code null} 时使用默认类型
     * @param storepass      存储密码（非空）
     * @param aliasToKeypass 别名到密钥密码的映射，为 {@code null} 时视为空映射
     */
    public KeyStoreSecretBox(Resource resource, @Nullable StoreType type, String storepass, @Nullable Map<String, String> aliasToKeypass) {
        Assert.notNull(resource, "resource must not be null");
        Assert.hasText(storepass, "storepass must not be empty");
        this.resource = resource;
        this.type = type != null ? type : StoreType.getDefault();
        this.storepass = storepass;
        this.aliasToKeypass = aliasToKeypass != null ? Collections.unmodifiableMap(aliasToKeypass) : Map.of();
    }

    @Override
    public String getStorePassword() {
        return storepass;
    }

    @Override
    public Map<String, String> getAliasToKeypassMapping() {
        return aliasToKeypass;
    }

    @Override
    public boolean containsAlias(String alias) {
        return KeyStoreUtils.containsAlias(getKeyStore(), alias);
    }

    @Override
    public List<String> getAliases() {
        return KeyStoreUtils.getAliases(getKeyStore());
    }

    @Override
    public <T extends SecretKey> T getSecretKey(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.getSecretKey(getKeyStore(), alias, getKeypass(alias));
    }

    @Override
    public List<X509Certificate> getCertificateChain(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.getCertificateChain(getKeyStore(), alias);
    }

    @Override
    public <T extends Certificate> T getCertificate(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.getCertificate(getKeyStore(), alias);
    }

    @Override
    public LocalDateTime getCertificateNotBefore(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.getCertificateNotBefore(getKeyStore(), alias);
    }

    @Override
    public LocalDateTime getCertificateNotAfter(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.getCertificateNotAfter(getKeyStore(), alias);
    }

    @Override
    public boolean isCertificateValid(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.isCertificateValid(getKeyStore(), alias);
    }

    @Override
    public <T extends PublicKey> T getPublicKey(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.getPublicKey(getKeyStore(), alias);
    }

    @Override
    public <T extends PrivateKey> T getPrivateKey(String alias) {
        requireAliasExists(alias);
        return KeyStoreUtils.getPrivateKey(getKeyStore(), alias, getKeypass(alias));
    }

    @Override
    public String toString() {
        return "SecretBox{aliases=" + getAliases() + '}';
    }

    // ------

    private void requireAliasExists(String alias) {
        if (!containsAlias(alias)) {
            throw new NoSuchElementException("alias not found: " + alias);
        }
    }

    private String getKeypass(String alias) {
        return aliasToKeypass.getOrDefault(alias, storepass);
    }

    private KeyStore getKeyStore() {
        var result = keyStore;
        if (result == null) {
            synchronized (this) {
                result = keyStore;
                if (result == null) {
                    result = KeyStoreUtils.loadKeyStore(resource, type, storepass);
                    keyStore = result;
                }
            }
        }
        return result;
    }
}

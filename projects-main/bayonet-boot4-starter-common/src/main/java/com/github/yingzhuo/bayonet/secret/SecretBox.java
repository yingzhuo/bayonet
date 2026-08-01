package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 对 {@link KeyStore} 的封装，提供便利的密钥/证书查询方法。
 * <p>通过 {@link #builder()} 构建实例，支持按别名查询密钥、证书链、公钥和私钥。</p>
 *
 * <pre>{@code
 * var box = SecretBox.builder()
 *         .resource(new ClassPathResource("keystore.p12"))
 *         .type(KeyStoreType.PKCS12)
 *         .storepass("storepass")
 *         .alias("jwt", "keypass")
 *         .alias("legacy")
 *         .build();
 * }</pre>
 *
 * @author 应卓
 * @see KeyStoreType
 * @see KeyStoreUtils
 * @since 4.1.1
 */
public final class SecretBox {

    private final Resource resource;
    private final KeyStoreType type;
    private final String storepass;
    private final Map<String, String> aliasToKeypass;
    private volatile @Nullable KeyStore keyStore;

    private SecretBox(Resource resource, KeyStoreType type, String storepass, Map<String, String> aliasToKeypass) {
        this.resource = resource;
        this.type = type;
        this.storepass = storepass;
        this.aliasToKeypass = aliasToKeypass;
    }

    /**
     * 获取 Builder 实例。
     *
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取 KeyStore 存储密码。
     *
     * @return 存储密码
     */
    public String getStorePassword() {
        return storepass;
    }

    /**
     * 获取别名到密钥密码的映射。
     * <p>未显式配置密钥密码的别名不在映射中，其密钥密码回退为存储密码。</p>
     *
     * @return 不可变映射
     */
    public Map<String, String> getAliasToKeypassMapping() {
        return Collections.unmodifiableMap(aliasToKeypass);
    }

    /**
     * 判断是否包含指定别名。
     *
     * @param alias 别名
     * @return {@code true} 表示包含
     */
    public boolean containsAlias(String alias) {
        return getAliases().contains(alias);
    }

    /**
     * 获取所有别名。
     *
     * @return 别名列表（不可变，非 {@code null}）
     */
    public List<String> getAliases() {
        return KeyStoreUtils.getAliases(getKeyStore());
    }

    /**
     * 获取指定别名的对称密钥。
     *
     * @param alias 别名
     * @param <T>   密钥类型
     * @return 对称密钥（非 {@code null}）
     * @throws IllegalArgumentException 别名不存在或获取失败
     */
    public <T extends SecretKey> T getSecretKey(String alias) {
        return KeyStoreUtils.getSecretKey(getKeyStore(), alias, getKeypass(alias));
    }

    /**
     * 获取指定别名的证书链。
     *
     * @param alias 别名
     * @return 证书链（可能为空）
     */
    public List<X509Certificate> getCertificateChain(String alias) {
        return KeyStoreUtils.getCertificateChain(getKeyStore(), alias);
    }

    /**
     * 获取指定别名的证书。
     *
     * @param alias 别名
     * @param <T>   证书类型
     * @return 证书（非 {@code null}）
     * @throws IllegalArgumentException 别名不存在或获取失败
     */
    public <T extends Certificate> T getCertificate(String alias) {
        return KeyStoreUtils.getCertificate(getKeyStore(), alias);
    }

    /**
     * 获取指定别名的公钥。
     *
     * @param alias 别名
     * @param <T>   公钥类型
     * @return 公钥（非 {@code null}）
     * @throws IllegalArgumentException 别名不存在或获取失败
     */
    public <T extends PublicKey> T getPublicKey(String alias) {
        return KeyStoreUtils.getPublicKey(getKeyStore(), alias);
    }

    /**
     * 获取指定别名的私钥。
     *
     * @param alias 别名
     * @param <T>   私钥类型
     * @return 私钥（非 {@code null}）
     * @throws IllegalArgumentException 别名不存在或获取失败
     */
    public <T extends PrivateKey> T getPrivateKey(String alias) {
        return KeyStoreUtils.getPrivateKey(getKeyStore(), alias, getKeypass(alias));
    }

    /**
     * 获取指定别名的 {@link KeyPair}。
     *
     * @param alias 别名
     * @return {@link KeyPair}（非 {@code null}）
     * @throws IllegalArgumentException 别名不存在或获取失败
     */
    public KeyPair getKeyPair(String alias) {
        return new KeyPair(getPublicKey(alias), getPrivateKey(alias));
    }

    // ------

    private String getKeypass(String alias) {
        return aliasToKeypass.getOrDefault(alias, storepass);
    }

    private KeyStore getKeyStore() {
        var result = keyStore;
        if (result == null) {
            synchronized (this) {
                result = keyStore;
                if (result == null) {
                    try (var in = resource.getInputStream()) {
                        result = KeyStoreUtils.loadKeyStore(in, type, storepass);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    keyStore = result;
                }
            }
        }
        return result;
    }

    // ------

    /**
     * {@link SecretBox} 构建器。
     */
    public static final class Builder {

        private final Map<String, String> aliasToKeypass = new LinkedHashMap<>();
        private @Nullable Resource resource;
        private KeyStoreType type = KeyStoreType.getDefault();
        private @Nullable String storepass;

        private Builder() {
        }

        /**
         * 设置 KeyStore 资源。
         *
         * @param resource KeyStore 资源（非 {@code null}）
         * @return 当前构建器
         */
        public Builder resource(Resource resource) {
            this.resource = resource;
            return this;
        }

        /**
         * 设置 KeyStore 类型。
         *
         * @param type 类型，为 {@code null} 时使用默认类型
         * @return 当前构建器
         * @see KeyStoreType
         */
        public Builder type(@Nullable KeyStoreType type) {
            this.type = type != null ? type : KeyStoreType.getDefault();
            return this;
        }

        /**
         * 设置 KeyStore 类型（字符串）。
         *
         * @param type 类型字符串，如 {@code "PKCS12"}、{@code "JKS"}，为 {@code null} 时使用默认类型
         * @return 当前构建器
         * @see KeyStoreType#toKeyStore(String)
         */
        public Builder type(@Nullable String type) {
            this.type = KeyStoreType.toKeyStore(type);
            return this;
        }

        /**
         * 设置 KeyStore 存储密码。
         *
         * @param storepass 存储密码（非空）
         * @return 当前构建器
         */
        public Builder storepass(String storepass) {
            this.storepass = storepass;
            return this;
        }

        /**
         * 添加别名到密钥密码的映射。
         * <p>未设置密钥密码时，回退使用存储密码。</p>
         *
         * @param alias   别名
         * @param keypass 密钥密码
         * @return 当前构建器
         */
        public Builder alias(String alias, @Nullable String keypass) {
            Assert.hasText(alias, "alias must not be empty");
            if (keypass != null) {
                this.aliasToKeypass.put(alias, keypass);
            }
            return this;
        }

        /**
         * 添加别名，其密钥密码使用存储密码。
         *
         * @param alias 别名
         * @return 当前构建器
         */
        public Builder alias(String alias) {
            return alias(alias, null);
        }

        /**
         * 批量设置别名到密钥密码的映射。
         *
         * @param map 别名到密钥密码的映射
         * @return 当前构建器
         */
        public Builder aliasToKeypass(Map<String, String> map) {
            Assert.notNull(map, "map must not be null");
            if (!map.isEmpty()) {
                this.aliasToKeypass.putAll(map);
            }
            return this;
        }

        /**
         * 构建 {@link SecretBox} 实例。
         *
         * @return {@link SecretBox} 实例（非 {@code null}）
         * @throws IllegalArgumentException 资源或存储密码缺失
         */
        public SecretBox build() {
            Assert.notNull(resource, "resource is required");
            Assert.hasText(storepass, "storepass is required");
            return new SecretBox(resource, type, storepass, Map.copyOf(aliasToKeypass));
        }
    }
}

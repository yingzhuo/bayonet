package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 对 {@link KeyStore} 的封装，提供便利的密钥/证书查询方法。
 * <p>通过 {@link #fromKeyStore()} 构建实例，支持按别名查询密钥、证书链、公钥和私钥。</p>
 *
 * <pre>{@code
 * var box = SecretBox.fromKeyStore()
 *         .resource(new ClassPathResource("keystore.p12"))
 *         .type(KeyStoreType.PKCS12)
 *         .storepass("storepass")
 *         .alias("jwt", "keypass")
 *         .build();
 * }</pre>
 *
 * <p>实现 {@link Iterable}，可直接迭代 KeyStore 中所有别名。</p>
 *
 * @author 应卓
 * @see KeyStoreType
 * @see KeyStoreUtils
 * @see KeyStoreSecretBox
 * @since 4.1.1
 */
public interface SecretBox extends Iterable<String> {

    /**
     * 获取 Builder 实例。
     *
     * @return Builder 实例
     */
    static KeyStoreKindBuilder fromKeyStore() {
        return new KeyStoreKindBuilder();
    }

    /**
     * 获取 KeyStore 存储密码。
     *
     * @return 存储密码
     */
    String getStorePassword();

    /**
     * 获取别名到密钥密码的映射。
     * <p>未显式配置密钥密码的别名不在映射中，其密钥密码回退为存储密码。</p>
     *
     * @return 不可变映射
     */
    Map<String, String> getAliasToKeypassMapping();

    /**
     * 判断是否包含指定别名。
     *
     * @param alias 别名
     * @return {@code true} 表示包含
     */
    boolean containsAlias(String alias);

    /**
     * 获取所有别名。
     *
     * @return 别名列表（不可变，非 {@code null}）
     */
    List<String> getAliases();

    /**
     * 获取包含的别名数量。
     *
     * @return 别名数量
     */
    default int size() {
        return getAliases().size();
    }

    /**
     * 获取指定别名的对称密钥。
     *
     * @param alias 别名
     * @param <T>   密钥类型
     * @return 对称密钥（非 {@code null}）
     * @throws NoSuchElementException 别名不存在
     */
    <T extends SecretKey> T getSecretKey(String alias);

    /**
     * 获取指定别名的证书链。
     *
     * @param alias 别名
     * @return 证书链（可能为空）
     * @throws NoSuchElementException 别名不存在
     */
    List<X509Certificate> getCertificateChain(String alias);

    /**
     * 获取指定别名的证书。
     *
     * @param alias 别名
     * @param <T>   证书类型
     * @return 证书（非 {@code null}）
     * @throws NoSuchElementException 别名不存在
     */
    <T extends Certificate> T getCertificate(String alias);

    /**
     * 获取指定别名的证书生效时间（NotBefore）。
     *
     * @param alias 别名
     * @return 证书生效时间（非 {@code null}）
     * @throws NoSuchElementException 别名不存在
     */
    LocalDateTime getCertificateNotBefore(String alias);

    /**
     * 获取指定别名的证书过期时间（NotAfter）。
     *
     * @param alias 别名
     * @return 证书过期时间（非 {@code null}）
     * @throws NoSuchElementException 别名不存在
     */
    LocalDateTime getCertificateNotAfter(String alias);

    /**
     * 判断当前时间是否在证书有效期内。
     *
     * @param alias 别名
     * @return {@code true} 表示当前时间在有效期内（{@code notBefore < now < notAfter}）
     * @throws NoSuchElementException 别名不存在
     */
    boolean isCertificateValid(String alias);

    /**
     * 获取指定别名的公钥。
     *
     * @param alias 别名
     * @param <T>   公钥类型
     * @return 公钥（非 {@code null}）
     * @throws NoSuchElementException 别名不存在
     */
    <T extends PublicKey> T getPublicKey(String alias);

    /**
     * 获取指定别名的私钥。
     *
     * @param alias 别名
     * @param <T>   私钥类型
     * @return 私钥（非 {@code null}）
     * @throws NoSuchElementException 别名不存在
     */
    <T extends PrivateKey> T getPrivateKey(String alias);

    /**
     * 获取指定别名的 {@link KeyPair}。
     *
     * @param alias 别名
     * @return {@link KeyPair}（非 {@code null}）
     * @throws NoSuchElementException 别名不存在
     */
    default KeyPair getKeyPair(String alias) {
        return new KeyPair(getPublicKey(alias), getPrivateKey(alias));
    }

    /**
     * 返回别名的迭代器。
     *
     * @return 别名迭代器（非 {@code null}）
     */
    @Override
    default Iterator<String> iterator() {
        return getAliases().iterator();
    }

    // ------

    /**
     * {@link SecretBox} 构建器
     */
    final class KeyStoreKindBuilder {

        private final Map<String, String> aliasToKeypass = new LinkedHashMap<>();
        private @Nullable Resource resource;
        private KeyStoreType type = KeyStoreType.getDefault();
        private @Nullable String storepass;

        /**
         * 设置 KeyStore 资源。
         *
         * @param resource KeyStore 资源（非 {@code null}）
         * @return 当前构建器
         */
        public KeyStoreKindBuilder resource(Resource resource) {
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
        public KeyStoreKindBuilder type(@Nullable KeyStoreType type) {
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
        public KeyStoreKindBuilder type(@Nullable String type) {
            this.type = KeyStoreType.toKeyStore(type);
            return this;
        }

        /**
         * 设置 KeyStore 存储密码。
         *
         * @param storepass 存储密码（非空）
         * @return 当前构建器
         */
        public KeyStoreKindBuilder storepass(String storepass) {
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
        public KeyStoreKindBuilder alias(String alias, @Nullable String keypass) {
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
        public KeyStoreKindBuilder alias(String alias) {
            return alias(alias, null);
        }

        /**
         * 批量设置别名到密钥密码的映射。
         *
         * @param map 别名到密钥密码的映射
         * @return 当前构建器
         */
        public KeyStoreKindBuilder aliasToKeypass(Map<String, String> map) {
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
            return new KeyStoreSecretBox(resource, type, storepass, Map.copyOf(aliasToKeypass));
        }
    }
}

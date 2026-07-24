package com.github.yingzhuo.bayonet.secret;

import com.github.yingzhuo.bayonet.utility.CloseUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * KeyStore 操作工具类。
 * <p>提供 KeyStore 加载、密钥/证书查询、别名检查等便捷方法。
 * 所有方法均会关闭传入的输入流。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyStoreUtils {

    /**
     * 从输入流加载 KeyStore。
     * <p>方法内部会关闭传入的输入流。</p>
     *
     * @param inputStream KeyStore 输入流（非 {@code null}）
     * @param type        KeyStore 类型，为 {@code null} 时使用默认类型 {@link KeyStoreType#PKCS12}
     * @param storepass   KeyStore 密码（非 {@code null}）
     * @return 已加载的 {@link KeyStore}（非 {@code null}）
     * @throws IllegalArgumentException 若参数为 {@code null} 或加载失败
     * @throws UncheckedIOException     读取输入流失败时抛出
     */
    public static KeyStore loadKeyStore(InputStream inputStream, @Nullable KeyStoreType type, String storepass) {
        Assert.notNull(inputStream, "inputStream is required");
        Assert.notNull(storepass, "storepass is required");

        type = Objects.requireNonNullElseGet(type, KeyStoreType::getDefault);

        try (var input = inputStream) {
            var keyStore = KeyStore.getInstance(type.name());
            keyStore.load(input, storepass.toCharArray());
            return keyStore;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            CloseUtils.closeQuietly(inputStream);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 从 KeyStore 中获取指定别名的密钥。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    密钥别名（非空）
     * @param keypass  密钥密码（非 {@code null}）
     * @param <T>      密钥类型
     * @return 密钥（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法、别名不存在或获取失败
     */
    @SuppressWarnings("unchecked")
    public static <T extends Key> T getKey(KeyStore keyStore, String alias, String keypass) {
        Assert.notNull(keyStore, "keyStore is required");
        Assert.hasText(alias, "alias is required");
        Assert.notNull(keypass, "keypass is required");

        T key;
        try {
            key = (T) keyStore.getKey(alias, keypass.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        if (key == null) {
            throw new IllegalArgumentException("cannot find key with alias: " + alias);
        }
        return key;
    }

    /**
     * 从 KeyStore 中获取指定别名的私钥。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    私钥别名（非空）
     * @param keypass  密钥密码（非 {@code null}）
     * @param <T>      私钥类型
     * @return 私钥（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法或获取失败
     */
    public static <T extends PrivateKey> T getPrivateKey(KeyStore keyStore, String alias, String keypass) {
        return getKey(keyStore, alias, keypass);
    }

    /**
     * 从 KeyStore 中获取指定别名的公钥。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    证书别名（非空）
     * @param <T>      公钥类型
     * @return 公钥（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法或获取失败
     */
    @SuppressWarnings("unchecked")
    public static <T extends PublicKey> T getPublicKey(KeyStore keyStore, String alias) {
        var cert = getCertificate(keyStore, alias);
        return (T) cert.getPublicKey();
    }

    /**
     * 从 KeyStore 中获取指定别名的证书链。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    证书别名（非空）
     * @return 证书链（非 {@code null}，可能为空）
     * @throws IllegalArgumentException 若参数非法或获取失败
     */
    public static List<X509Certificate> getCertificateChain(KeyStore keyStore, String alias) {
        Assert.notNull(keyStore, "keyStore is required");
        Assert.hasText(alias, "alias is required");

        try {
            var chain = keyStore.getCertificateChain(alias);
            if (chain == null) {
                return List.of();
            }
            return Arrays.stream(chain)
                    .map(c -> (X509Certificate) c)
                    .toList();
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 从 KeyStore 中获取指定别名的证书。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    证书别名（非空）
     * @param <T>      证书类型
     * @return 证书（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法、别名不存在或获取失败
     */
    @SuppressWarnings("unchecked")
    public static <T extends Certificate> T getCertificate(KeyStore keyStore, String alias) {
        Assert.notNull(keyStore, "keyStore is required");
        Assert.hasText(alias, "alias is required");

        T certificate;
        try {
            certificate = (T) keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        if (certificate == null) {
            throw new IllegalArgumentException("cannot find certificate with alias: " + alias);
        }
        return certificate;
    }

    /**
     * 从 KeyStore 中获取指定别名的 {@link KeyPair}。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    别名（非空）
     * @param keypass  密钥密码（非 {@code null}）
     * @return {@link KeyPair}（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法或获取失败
     */
    public static KeyPair getKeyPair(KeyStore keyStore, String alias, String keypass) {
        return new KeyPair(getPublicKey(keyStore, alias), getPrivateKey(keyStore, alias, keypass));
    }

    /**
     * 获取指定别名的签名算法名称。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    别名（非空）
     * @return 签名算法名称
     * @throws IllegalArgumentException 若参数非法或证书非 {@link X509Certificate}
     */
    public static String getSigAlgName(KeyStore keyStore, String alias) {
        return getSigAlgAttr(keyStore, alias, X509Certificate::getSigAlgName, "SigAlgName");
    }

    /**
     * 获取指定别名的签名算法 OID。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    别名（非空）
     * @return 签名算法 OID
     * @throws IllegalArgumentException 若参数非法或证书非 {@link X509Certificate}
     */
    public static String getSigAlgOID(KeyStore keyStore, String alias) {
        return getSigAlgAttr(keyStore, alias, X509Certificate::getSigAlgOID, "SigAlgOID");
    }

    /**
     * 从 KeyStore 中获取指定别名的对称密钥。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    别名（非空）
     * @param keypass  密钥密码（非 {@code null}）
     * @param <T>      密钥类型
     * @return 对称密钥（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法或获取失败
     */
    public static <T extends SecretKey> T getSecretKey(KeyStore keyStore, String alias, String keypass) {
        return getKey(keyStore, alias, keypass);
    }

    /**
     * 获取 KeyStore 中所有别名。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @return 别名列表（不可变，非 {@code null}）
     * @throws IllegalArgumentException 若参数为 {@code null} 或获取失败
     */
    public static List<String> getAliases(KeyStore keyStore) {
        Assert.notNull(keyStore, "keyStore is required");

        try {
            return Collections.unmodifiableList(Collections.list(keyStore.aliases()));
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 判断 KeyStore 是否包含指定别名。
     *
     * @param keyStore 已加载的 KeyStore（非 {@code null}）
     * @param alias    别名（非空）
     * @return {@code true} 表示包含
     * @throws IllegalArgumentException 若参数为 {@code null} 或获取失败
     */
    public static boolean containsAlias(KeyStore keyStore, String alias) {
        Assert.notNull(keyStore, "keyStore is required");
        Assert.hasText(alias, "alias is required");

        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    // ---

    private static <T> T getSigAlgAttr(KeyStore keyStore, String alias, Function<X509Certificate, T> extractor, String attrName) {
        var cert = getCertificate(keyStore, alias);
        if (cert instanceof X509Certificate x509Cert) {
            return extractor.apply(x509Cert);
        }
        throw new IllegalArgumentException("cannot get " + attrName);
    }
}

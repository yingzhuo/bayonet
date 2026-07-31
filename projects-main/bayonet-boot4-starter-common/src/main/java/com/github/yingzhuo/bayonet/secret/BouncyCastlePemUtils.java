package com.github.yingzhuo.bayonet.secret;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 基于 Bouncy Castle 的 PEM 文件工具类。
 * <p>提供从 PEM 文件中加载公钥和私钥的静态方法，兼容 PKCS#1 和 PKCS#8 格式，
 * 支持加密/未加密的私钥以及证书中包含的公钥。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BouncyCastlePemUtils {

    /**
     * 从 PEM 资源文件中加载公钥。
     * <p>支持以下 PEM 格式：</p>
     * <ul>
     *   <li>{@code SubjectPublicKeyInfo}（SPKI，即 {@code -----BEGIN PUBLIC KEY-----}）</li>
     *   <li>{@code PEMKeyPair}（包含公钥和私钥对）</li>
     *   <li>{@code X509CertificateHolder}（X.509 证书，从中提取公钥）</li>
     * </ul>
     *
     * @param resource PEM 资源文件，不能为 {@code null}
     * @param <T>      公钥类型
     * @return 公钥实例
     * @throws IllegalArgumentException 资源为空、格式不支持或解析失败时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T extends PublicKey> T loadPublicKey(Resource resource) {
        Assert.notNull(resource, "resource must not be null");

        try {
            var converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);

            try (var parser = createParser(resource)) {
                var obj = parser.readObject();

                if (obj instanceof SubjectPublicKeyInfo spki) {
                    return (T) converter.getPublicKey(spki);
                }

                if (obj instanceof X509CertificateHolder cert) {
                    return (T) converter.getPublicKey(cert.getSubjectPublicKeyInfo());
                }

                if (obj instanceof PEMKeyPair keyPair) {
                    return (T) converter.getPublicKey(keyPair.getPublicKeyInfo());
                }
            }

            throw new IllegalArgumentException("Unsupported PEM object: " + resource.getDescription());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load public key from: " + resource.getDescription(), e);
        }
    }

    /**
     * 从 PEM 资源文件中加载私钥。
     * <p>支持以下 PEM 格式：</p>
     * <ul>
     *   <li>{@code PrivateKeyInfo}（PKCS#8 未加密，即 {@code -----BEGIN PRIVATE KEY-----}）</li>
     *   <li>{@code PKCS8EncryptedPrivateKeyInfo}（PKCS#8 加密，即 {@code -----BEGIN ENCRYPTED PRIVATE KEY-----}）</li>
     *   <li>{@code PEMKeyPair}（PKCS#1 未加密，即 {@code -----BEGIN RSA PRIVATE KEY-----}）</li>
     *   <li>{@code PEMEncryptedKeyPair}（PKCS#1 加密，即 {@code -----BEGIN RSA ENCRYPTED PRIVATE KEY-----}）</li>
     * </ul>
     *
     * @param resource PEM 资源文件，不能为 {@code null}
     * @param password 私钥密码，加密私钥时必须提供，未加密私钥时可传 {@code null}
     * @param <T>      私钥类型
     * @return 私钥实例
     * @throws IllegalArgumentException 资源为空、格式不支持、密码错误或解析失败时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T extends PrivateKey> T loadPrivateKey(Resource resource, @Nullable String password) {
        Assert.notNull(resource, "resource must not be null");

        try {
            var converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);

            try (var parser = createParser(resource)) {
                var obj = parser.readObject();

                if (obj == null) {
                    throw new IllegalArgumentException("Empty PEM file: " + resource.getDescription());
                }

                // Encrypted PKCS#8 (-----BEGIN ENCRYPTED PRIVATE KEY-----)
                if (obj instanceof PKCS8EncryptedPrivateKeyInfo encryptedPkcs8) {
                    if (password == null) {
                        throw new IllegalArgumentException("Password required for encrypted key: " + resource.getDescription());
                    }
                    var decryptor = new JceOpenSSLPKCS8DecryptorProviderBuilder()
                            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                            .build(password.toCharArray());
                    var privateKeyInfo = encryptedPkcs8.decryptPrivateKeyInfo(decryptor);
                    return (T) converter.getPrivateKey(privateKeyInfo);
                }

                // Unencrypted PKCS#8 (-----BEGIN PRIVATE KEY-----)
                if (obj instanceof PrivateKeyInfo pki) {
                    return (T) converter.getPrivateKey(pki);
                }

                // Encrypted PKCS#1 (-----BEGIN RSA ENCRYPTED PRIVATE KEY-----)
                if (obj instanceof PEMEncryptedKeyPair encryptedKeyPair) {
                    if (password == null) {
                        throw new IllegalArgumentException("Password required for encrypted key: " + resource.getDescription());
                    }
                    var decryptor = new JcePEMDecryptorProviderBuilder()
                            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                            .build(password.toCharArray());
                    var keyPair = encryptedKeyPair.decryptKeyPair(decryptor);
                    return (T) converter.getPrivateKey(keyPair.getPrivateKeyInfo());
                }

                // Unencrypted PKCS#1 (-----BEGIN RSA PRIVATE KEY-----)
                if (obj instanceof PEMKeyPair keyPair) {
                    return (T) converter.getPrivateKey(keyPair.getPrivateKeyInfo());
                }
            }

            throw new IllegalArgumentException("Unsupported PEM object: " + resource.getDescription());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load private key from: " + resource.getDescription(), e);
        }
    }

    /**
     * 从 PEM 资源文件中加载 {@link KeyPair}。
     * <p>支持以下 PEM 格式：</p>
     * <ul>
     *   <li>{@code PEMKeyPair}（PKCS#1，即 {@code -----BEGIN RSA PRIVATE KEY-----} 等）</li>
     *   <li>{@code PEMEncryptedKeyPair}（PKCS#1 加密）</li>
     * </ul>
     *
     * @param resource PEM 资源文件，不能为 {@code null}
     * @param password 私钥密码，加密私钥时必须提供，未加密私钥时可传 {@code null}
     * @return {@link KeyPair} 实例
     * @throws IllegalArgumentException 资源为空、格式不支持、密码错误或解析失败时抛出
     */
    public static KeyPair loadKeyPair(Resource resource, @Nullable String password) {
        Assert.notNull(resource, "resource must not be null");

        try {
            var converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);

            try (var parser = createParser(resource)) {
                var obj = parser.readObject();

                if (obj == null) {
                    throw new IllegalArgumentException("Empty PEM file: " + resource.getDescription());
                }

                if (obj instanceof PEMKeyPair keyPair) {
                    return converter.getKeyPair(keyPair);
                }

                if (obj instanceof PEMEncryptedKeyPair encryptedKeyPair) {
                    if (password == null) {
                        throw new IllegalArgumentException("Password required for encrypted key: " + resource.getDescription());
                    }
                    var decryptor = new JcePEMDecryptorProviderBuilder()
                            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                            .build(password.toCharArray());
                    var keyPair = encryptedKeyPair.decryptKeyPair(decryptor);
                    return converter.getKeyPair(keyPair);
                }
            }

            throw new IllegalArgumentException("Unsupported PEM object: " + resource.getDescription());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load key pair from: " + resource.getDescription(), e);
        }
    }

    // ------

    private static PEMParser createParser(Resource resource) {
        if (resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException(String.format("Resource '%s' does not exist or is not readable.", resource.getDescription()));
        }
        try {
            var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return new PEMParser(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

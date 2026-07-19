package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * RSA 加密解密工具类。
 * <p>提供基于 RSA 算法的加密、解密操作，支持短数据单次加密和长数据自动分块加密。
 *
 * <pre>{@code
 * // 生成密钥对（2048 位）
 * var keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
 *
 * // 短数据加密
 * String encrypted = RSACryptoUtils.encrypt("hello", keyPair.getPublic());
 * String decrypted = RSACryptoUtils.decrypt(encrypted, keyPair.getPrivate());
 *
 * // 长数据加密（自动分块）
 * String longEncrypted = RSACryptoUtils.encryptLong(longText, keyPair.getPublic());
 * String longDecrypted = RSACryptoUtils.decryptLong(longEncrypted, keyPair.getPrivate());
 * }</pre>
 *
 * @author 应卓
 * @see SignatureUtils
 * @see Cipher
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RSACryptoUtils {

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * 加密短数据（单次 RSA 操作）。
     * <p>RSA 单次加密的最大数据长度受密钥长度和填充算法限制。
     * 例如 2048 位密钥 + PKCS1Padding 最多加密 245 字节。
     * 超长数据请使用 {@link #encryptLong}。</p>
     *
     * @param plainText 明文（非 {@code null}）
     * @param publicKey 公钥（非 {@code null}）
     * @return Base64 编码的密文
     * @throws IllegalArgumentException 若参数为 {@code null} 或加密失败
     */
    public static String encrypt(String plainText, PublicKey publicKey) {
        Assert.notNull(plainText, "plainText must not be null");
        Assert.notNull(publicKey, "publicKey must not be null");

        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            var encryptedBytes = cipher.doFinal(plainText.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to encrypt: " + e.getMessage(), e);
        }
    }

    /**
     * 解密短数据（单次 RSA 操作）。
     *
     * @param cipherTextBase64 Base64 编码的密文（非 {@code null}）
     * @param privateKey       私钥（非 {@code null}）
     * @return 解密后的明文
     * @throws IllegalArgumentException 若参数为 {@code null} 或解密失败
     */
    public static String decrypt(String cipherTextBase64, PrivateKey privateKey) {
        Assert.notNull(cipherTextBase64, "cipherTextBase64 must not be null");
        Assert.notNull(privateKey, "privateKey must not be null");

        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            var decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherTextBase64));
            return new String(decryptedBytes, UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt: " + e.getMessage(), e);
        }
    }

    /**
     * 加密长数据（自动分块 RSA 加密）。
     * <p>当明文超过单次 RSA 操作限制时，自动按密钥长度分块加密。
     * 各分块密文以 {@code |} 分隔拼接。</p>
     *
     * <p><strong>注意：</strong>此方法仅支持 RSA 密钥，且基于 PKCS1Padding（11 字节填充开销）计算分块大小。</p>
     *
     * @param plainText 明文（非 {@code null}）
     * @param publicKey RSA 公钥（非 {@code null}）
     * @return 分块密文（Base64 编码，以 {@code |} 分隔）
     * @throws IllegalArgumentException 若参数为 {@code null}、密钥非 RSA 或加密失败
     */
    public static String encryptLong(String plainText, PublicKey publicKey) {
        Assert.notNull(plainText, "plainText must not be null");
        Assert.notNull(publicKey, "publicKey must not be null");

        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            var blockSize = deriveBlockSize(publicKey);
            var data = plainText.getBytes(UTF_8);
            var result = new StringBuilder();

            for (int offset = 0; offset < data.length; offset += blockSize) {
                int end = Math.min(offset + blockSize, data.length);
                var block = cipher.doFinal(data, offset, end - offset);
                result.append(Base64.getEncoder().encodeToString(block));
                if (end < data.length) {
                    result.append('|');
                }
            }
            return result.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to encrypt long data: " + e.getMessage(), e);
        }
    }

    /**
     * 解密长数据（自动分块 RSA 解密）。
     *
     * @param cipherTextBase64 分块密文（Base64 编码，以 {@code |} 分隔，非 {@code null}）
     * @param privateKey       RSA 私钥（非 {@code null}）
     * @return 解密后的明文
     * @throws IllegalArgumentException 若参数为 {@code null}、密钥非 RSA 或解密失败
     */
    public static String decryptLong(String cipherTextBase64, PrivateKey privateKey) {
        Assert.notNull(cipherTextBase64, "cipherTextBase64 must not be null");
        Assert.notNull(privateKey, "privateKey must not be null");

        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            var blocks = cipherTextBase64.split("\\|");
            var bos = new ByteArrayOutputStream(cipherTextBase64.length());

            for (var block : blocks) {
                var decryptedBlock = cipher.doFinal(Base64.getDecoder().decode(block));
                bos.writeBytes(decryptedBlock);
            }
            return bos.toString(UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt long data: " + e.getMessage(), e);
        }
    }

    /**
     * 从 RSA 密钥推导分块大小（PKCS1Padding 填充开销 11 字节）。
     */
    private static int deriveBlockSize(Key key) {
        if (key instanceof RSAKey rsaKey) {
            return rsaKey.getModulus().bitLength() / 8 - 11;
        }
        throw new IllegalArgumentException("encryptLong/decryptLong only support RSA keys, got: " + key.getClass().getName());
    }
}

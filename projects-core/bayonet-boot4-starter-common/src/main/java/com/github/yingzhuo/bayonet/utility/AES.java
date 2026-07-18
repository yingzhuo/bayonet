package com.github.yingzhuo.bayonet.utility;

import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 加密解密器。
 * <p>使用 AES/GCM/NoPadding 模式，GCM 标签长度 128 位。
 * 加密时自动生成 IV 并拼接到密文头部（前 12 字节），解密时自动提取。</p>
 *
 * <pre>{@code
 * var aes = new AES(AES.generateKey());
 *
 * byte[] encrypted = aes.encrypt(data);
 * byte[] decrypted = aes.decrypt(encrypted);
 * }</pre>
 *
 * @see #generateKey()
 * @author 应卓
 */
public final class AES {

    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int DEFAULT_KEY_SIZE = 256;

    private final SecretKey secretKey;

    /**
     * 构造器（使用 {@link SecretKey}）。
     *
     * @param secretKey AES 密钥（非 {@code null}）
     * @throws IllegalArgumentException 若参数为 {@code null}
     */
    public AES(SecretKey secretKey) {
        Assert.notNull(secretKey, "secretKey must not be null");
        this.secretKey = secretKey;
    }

    /**
     * 构造器（使用密钥字节数组）。
     *
     * @param secretKey AES 密钥字节数组（非 {@code null}）
     * @throws IllegalArgumentException 若参数为 {@code null}
     */
    public AES(byte[] secretKey) {
        Assert.notNull(secretKey, "secretKey must not be null");
        this.secretKey = AES.restoreKey(secretKey);
    }

    /**
     * 构造器（使用 URL-safe Base64 编码的密钥字符串）。
     *
     * @param base64UrlEncodedKey URL-safe Base64 编码的 AES 密钥（非空）
     * @throws IllegalArgumentException 若参数为空或 Base64 解码失败
     */
    public AES(String base64UrlEncodedKey) {
        Assert.hasText(base64UrlEncodedKey, "base64UrlEncodedKey must not be null or empty");
        this.secretKey = AES.restoreKey(Base64.getUrlDecoder().decode(base64UrlEncodedKey));
    }

    // ------

    /**
     * 生成 AES 密钥（256 位）。
     *
     * @return AES {@link SecretKey}
     * @throws IllegalArgumentException 若密钥生成失败
     */
    public static SecretKey generateKey() {
        return generateKey(DEFAULT_KEY_SIZE);
    }

    /**
     * 生成指定长度的 AES 密钥。
     *
     * @param keySize 密钥长度（位），支持 128、192、256
     * @return AES {@link SecretKey}
     * @throws IllegalArgumentException 若密钥生成失败
     */
    public static SecretKey generateKey(int keySize) {
        try {
            var generator = KeyGenerator.getInstance("AES");
            generator.init(keySize, new SecureRandom());
            return generator.generateKey();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to generate AES key: " + e.getMessage(), e);
        }
    }

    /**
     * 从字节数组恢复 AES 密钥。
     *
     * @param encoded 密钥字节数组
     * @return AES {@link SecretKey}
     * @throws IllegalArgumentException 若参数为 {@code null}
     */
    public static SecretKey restoreKey(byte[] encoded) {
        Assert.notNull(encoded, "encoded must not be null");
        return new SecretKeySpec(encoded, "AES");
    }

    // ------

    /**
     * 加密数据（AES/GCM/NoPadding）。
     * <p>返回的密文前 12 字节为 IV，后续为实际密文 + GCM 标签。</p>
     *
     * @param data 待加密数据
     * @return 加密结果（IV + 密文 + GCM 标签）
     * @throws IllegalArgumentException 若参数为 {@code null} 或加密失败
     */
    public byte[] encrypt(byte[] data) {
        Assert.notNull(data, "data must not be null");

        try {
            var iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            var cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            var ciphertext = cipher.doFinal(data);

            var result = new byte[GCM_IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, GCM_IV_LENGTH);
            System.arraycopy(ciphertext, 0, result, GCM_IV_LENGTH, ciphertext.length);
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to encrypt data: " + e.getMessage(), e);
        }
    }

    /**
     * 解密数据（AES/GCM/NoPadding）。
     * <p>要求密文前 12 字节为加密时生成的 IV。</p>
     *
     * @param encryptedData 加密数据（IV + 密文 + GCM 标签）
     * @return 解密后的原始数据
     * @throws IllegalArgumentException 若参数为 {@code null}、密文过短或解密失败
     */
    public byte[] decrypt(byte[] encryptedData) {
        Assert.notNull(encryptedData, "encryptedData must not be null");

        if (encryptedData.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException("encryptedData is too short");
        }

        try {
            var cipher = Cipher.getInstance(AES_GCM);
            var iv = copyOf(encryptedData, 0, GCM_IV_LENGTH);
            var ciphertext = copyOf(encryptedData, GCM_IV_LENGTH, encryptedData.length - GCM_IV_LENGTH);

            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt data: " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前密钥的 URL-safe Base64 编码字符串。
     *
     * @return URL-safe Base64 编码的密钥
     */
    public String getSecretKeyAsBase64() {
        return Base64.getUrlEncoder().encodeToString(secretKey.getEncoded());
    }

    private byte[] copyOf(byte[] src, int offset, int length) {
        var dest = new byte[length];
        System.arraycopy(src, offset, dest, 0, length);
        return dest;
    }
}

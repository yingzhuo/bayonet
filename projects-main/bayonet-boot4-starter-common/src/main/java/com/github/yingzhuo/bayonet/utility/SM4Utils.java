package com.github.yingzhuo.bayonet.utility;

import com.github.yingzhuo.bayonet.context.BouncyCastleInstallingEnvironmentPostProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * SM4 对称加密工具类。
 *
 * <p>SM4 为国产商用密码分组算法，分组长度 128 位（16 字节），密钥长度固定 128 位（16 字节）。
 * 基于 BouncyCastle 的 {@code "BC"} JCA Provider 实现，通过 {@link Cipher} 完成加解密。</p>
 *
 * <p><b>使用前提：</b>BouncyCastle 的 {@code "BC"} Provider 必须已注册到 JCA。
 * 在 Spring Boot 环境下，bayonet 通过 {@link BouncyCastleInstallingEnvironmentPostProcessor}
 * 自动完成注册；在普通 Java 环境下，需先自行注册，否则所有方法会因找不到 Provider 而失败：</p>
 *
 * <pre>{@code
 * Security.addProvider(new BouncyCastleProvider());
 * }</pre>
 *
 * <p>支持的模式：</p>
 * <ul>
 *   <li>ECB — 无 IV，相同明文得到相同密文，安全性较低，<b>不推荐</b>（各 ECB 方法已标注
 *       {@code @Deprecated}）</li>
 *   <li>CBC — 需要 16 字节 IV（{@link #generateIv()}），IV 不需保密但必须不可预测，
 *       由调用方自行生成、传输并在解密时传入</li>
 *   <li>GCM — 认证加密模式，加密时自动生成 12 字节 IV 并拼接在密文头部，推荐用于一般加密场景</li>
 * </ul>
 *
 * @author 应卓
 * @see Cipher
 * @see BouncyCastleInstallingEnvironmentPostProcessor
 * @see HexUtils
 * @see AES
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SM4Utils {

    private static final String ALGORITHM_NAME = "SM4";
    private static final String TRANSFORMATION_ECB = "SM4/ECB/PKCS5Padding";
    private static final String TRANSFORMATION_CBC = "SM4/CBC/PKCS5Padding";
    private static final String TRANSFORMATION_GCM = "SM4/GCM/NoPadding";
    private static final int KEY_SIZE = 128;           // SM4 固定 128bit
    private static final int BLOCK_SIZE = 16;          // 128bit / 8 = 16 字节
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // ------

    /**
     * 生成 SM4 密钥（128bit，16 字节），返回原始字节。
     *
     * @return 密钥字节数组（16 字节）
     * @throws RuntimeException 密钥生成失败
     */
    public static byte[] generateKey() {
        try {
            var kg = KeyGenerator.getInstance(ALGORITHM_NAME, "BC");
            kg.init(KEY_SIZE, new SecureRandom());
            return kg.generateKey().getEncoded();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to generate SM4 key: " + e.getMessage(), e);
        }
    }

    /**
     * 生成 SM4 密钥，返回 Hex 字符串。
     *
     * @return 密钥 Hex 字符串
     */
    public static String generateKeyHex() {
        return HexUtils.encodeToString(generateKey());
    }

    /**
     * 生成 SM4 密钥，返回 Base64 字符串。
     *
     * @return 密钥 Base64 字符串
     */
    public static String generateKeyBase64() {
        return Base64.getEncoder().encodeToString(generateKey());
    }

    /**
     * 生成 CBC 模式使用的 IV（16 字节），返回原始字节。
     *
     * @return IV 字节数组（16 字节）
     */
    public static byte[] generateIv() {
        byte[] iv = new byte[BLOCK_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 生成 CBC 模式使用的 IV，返回 Hex 字符串。
     *
     * @return IV Hex 字符串
     */
    public static String generateIvHex() {
        return HexUtils.encodeToString(generateIv());
    }

    /**
     * 生成 CBC 模式使用的 IV，返回 Base64 字符串。
     *
     * @return IV Base64 字符串
     */
    public static String generateIvBase64() {
        return Base64.getEncoder().encodeToString(generateIv());
    }

    // ------

    /**
     * ECB 加密（byte[] 入参，返回密文字节）。
     *
     * @param key       密钥（16 字节）
     * @param plaintext 明文
     * @return 密文字节
     * @deprecated ECB 模式无 IV，相同明文产生相同密文，安全性低；仅在加密随机数据等场景下使用
     */
    @Deprecated
    public static byte[] encryptEcb(byte[] key, byte[] plaintext) {
        return doCipher(key, null, plaintext, TRANSFORMATION_ECB, Cipher.ENCRYPT_MODE);
    }

    /**
     * ECB 加密（String 入参，返回 Hex 密文）。
     *
     * @param keyHex    密钥 Hex 字符串
     * @param plaintext 明文
     * @return Hex 密文
     * @deprecated ECB 模式无 IV，相同明文产生相同密文，安全性低；仅在加密随机数据等场景下使用
     */
    @Deprecated
    public static String encryptEcbHex(String keyHex, String plaintext) {
        Assert.notNull(keyHex, "keyHex must not be null");
        Assert.notNull(plaintext, "plaintext must not be null");
        byte[] cipherBytes = encryptEcb(HexUtils.decodeToBytes(keyHex), plaintext.getBytes(UTF_8));
        return HexUtils.encodeToString(cipherBytes);
    }

    /**
     * ECB 加密（String 入参，返回 Base64 密文）。
     *
     * @param keyBase64 密钥 Base64 字符串
     * @param plaintext 明文
     * @return Base64 密文
     * @deprecated ECB 模式无 IV，相同明文产生相同密文，安全性低；仅在加密随机数据等场景下使用
     */
    @Deprecated
    public static String encryptEcbBase64(String keyBase64, String plaintext) {
        Assert.notNull(keyBase64, "keyBase64 must not be null");
        Assert.notNull(plaintext, "plaintext must not be null");
        byte[] key = Base64.getDecoder().decode(keyBase64);
        byte[] cipherBytes = encryptEcb(key, plaintext.getBytes(UTF_8));
        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    /**
     * ECB 解密（byte[] 入参）。
     *
     * @param key        密钥（16 字节）
     * @param ciphertext 密文
     * @return 明文字节
     * @deprecated ECB 模式无 IV，相同明文产生相同密文，安全性低；仅在加密随机数据等场景下使用
     */
    @Deprecated
    public static byte[] decryptEcb(byte[] key, byte[] ciphertext) {
        return doCipher(key, null, ciphertext, TRANSFORMATION_ECB, Cipher.DECRYPT_MODE);
    }

    /**
     * ECB 解密（Hex 密文 → 明文字符串）。
     *
     * @param keyHex    密钥 Hex 字符串
     * @param cipherHex Hex 密文
     * @return 明文字符串
     * @deprecated ECB 模式无 IV，相同明文产生相同密文，安全性低；仅在加密随机数据等场景下使用
     */
    @Deprecated
    public static String decryptEcbHex(String keyHex, String cipherHex) {
        Assert.notNull(keyHex, "keyHex must not be null");
        Assert.notNull(cipherHex, "cipherHex must not be null");
        byte[] plainBytes = decryptEcb(HexUtils.decodeToBytes(keyHex), HexUtils.decodeToBytes(cipherHex));
        return new String(plainBytes, UTF_8);
    }

    /**
     * ECB 解密（Base64 密文 → 明文字符串）。
     *
     * @param keyBase64   密钥 Base64 字符串
     * @param cipherBase64 Base64 密文
     * @return 明文字符串
     * @deprecated ECB 模式无 IV，相同明文产生相同密文，安全性低；仅在加密随机数据等场景下使用
     */
    @Deprecated
    public static String decryptEcbBase64(String keyBase64, String cipherBase64) {
        Assert.notNull(keyBase64, "keyBase64 must not be null");
        Assert.notNull(cipherBase64, "cipherBase64 must not be null");
        byte[] key = Base64.getDecoder().decode(keyBase64);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherBase64);
        byte[] plainBytes = decryptEcb(key, cipherBytes);
        return new String(plainBytes, UTF_8);
    }

    // ------

    /**
     * CBC 加密（byte[] 入参，返回密文字节）。
     *
     * @param key       密钥（16 字节）
     * @param iv        IV（16 字节），由 {@link #generateIv()} 生成
     * @param plaintext 明文
     * @return 密文字节
     */
    public static byte[] encryptCbc(byte[] key, byte[] iv, byte[] plaintext) {
        return doCipher(key, iv, plaintext, TRANSFORMATION_CBC, Cipher.ENCRYPT_MODE);
    }

    /**
     * CBC 加密（Hex 入参，返回 Hex 密文）。
     *
     * @param keyHex    密钥 Hex 字符串
     * @param ivHex     IV Hex 字符串
     * @param plaintext 明文
     * @return Hex 密文
     */
    public static String encryptCbcHex(String keyHex, String ivHex, String plaintext) {
        Assert.notNull(keyHex, "keyHex must not be null");
        Assert.notNull(ivHex, "ivHex must not be null");
        Assert.notNull(plaintext, "plaintext must not be null");
        byte[] key = HexUtils.decodeToBytes(keyHex);
        byte[] iv = HexUtils.decodeToBytes(ivHex);
        byte[] cipherBytes = encryptCbc(key, iv, plaintext.getBytes(UTF_8));
        return HexUtils.encodeToString(cipherBytes);
    }

    /**
     * CBC 加密（Base64 入参，返回 Base64 密文）。
     *
     * @param keyBase64 密钥 Base64 字符串
     * @param ivBase64  IV Base64 字符串
     * @param plaintext 明文
     * @return Base64 密文
     */
    public static String encryptCbcBase64(String keyBase64, String ivBase64, String plaintext) {
        Assert.notNull(keyBase64, "keyBase64 must not be null");
        Assert.notNull(ivBase64, "ivBase64 must not be null");
        Assert.notNull(plaintext, "plaintext must not be null");
        byte[] key = Base64.getDecoder().decode(keyBase64);
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] cipherBytes = encryptCbc(key, iv, plaintext.getBytes(UTF_8));
        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    /**
     * CBC 解密（byte[] 入参）。
     *
     * @param key        密钥（16 字节）
     * @param iv         IV（16 字节）
     * @param ciphertext 密文
     * @return 明文字节
     */
    public static byte[] decryptCbc(byte[] key, byte[] iv, byte[] ciphertext) {
        return doCipher(key, iv, ciphertext, TRANSFORMATION_CBC, Cipher.DECRYPT_MODE);
    }

    /**
     * CBC 解密（Hex 密文 → 明文字符串）。
     *
     * @param keyHex    密钥 Hex 字符串
     * @param ivHex     IV Hex 字符串
     * @param cipherHex Hex 密文
     * @return 明文字符串
     */
    public static String decryptCbcHex(String keyHex, String ivHex, String cipherHex) {
        Assert.notNull(keyHex, "keyHex must not be null");
        Assert.notNull(ivHex, "ivHex must not be null");
        Assert.notNull(cipherHex, "cipherHex must not be null");
        byte[] key = HexUtils.decodeToBytes(keyHex);
        byte[] iv = HexUtils.decodeToBytes(ivHex);
        byte[] plainBytes = decryptCbc(key, iv, HexUtils.decodeToBytes(cipherHex));
        return new String(plainBytes, UTF_8);
    }

    /**
     * CBC 解密（Base64 密文 → 明文字符串）。
     *
     * @param keyBase64    密钥 Base64 字符串
     * @param ivBase64     IV Base64 字符串
     * @param cipherBase64 Base64 密文
     * @return 明文字符串
     */
    public static String decryptCbcBase64(String keyBase64, String ivBase64, String cipherBase64) {
        Assert.notNull(keyBase64, "keyBase64 must not be null");
        Assert.notNull(ivBase64, "ivBase64 must not be null");
        Assert.notNull(cipherBase64, "cipherBase64 must not be null");
        byte[] key = Base64.getDecoder().decode(keyBase64);
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherBase64);
        byte[] plainBytes = decryptCbc(key, iv, cipherBytes);
        return new String(plainBytes, UTF_8);
    }

    // ------

    /**
     * GCM 加密（byte[] 入参）。
     * <p>自动生成 12 字节 IV，返回结果为 {@code IV + 密文 + GCM 标签} 拼接的字节数组。</p>
     *
     * @param key       密钥（16 字节）
     * @param data      明文
     * @return 密文（IV + 密文 + GCM 标签）
     * @throws RuntimeException 加密失败（如密钥非法）
     */
    public static byte[] encryptGcm(byte[] key, byte[] data) {
        requireKey(key);
        Assert.notNull(data, "data must not be null");
        try {
            var iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            var cipher = Cipher.getInstance(TRANSFORMATION_GCM, "BC");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM_NAME), new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            var ciphertext = cipher.doFinal(data);
            var result = new byte[GCM_IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, GCM_IV_LENGTH);
            System.arraycopy(ciphertext, 0, result, GCM_IV_LENGTH, ciphertext.length);
            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to encrypt with SM4/GCM: " + e.getMessage(), e);
        }
    }

    /**
     * GCM 加密（Hex 入参，返回 Hex 密文）。
     *
     * @param keyHex    密钥 Hex 字符串
     * @param plaintext 明文
     * @return Hex 密文（IV + 密文 + GCM 标签）
     */
    public static String encryptGcmHex(String keyHex, String plaintext) {
        Assert.notNull(keyHex, "keyHex must not be null");
        Assert.notNull(plaintext, "plaintext must not be null");
        return HexUtils.encodeToString(encryptGcm(HexUtils.decodeToBytes(keyHex), plaintext.getBytes(UTF_8)));
    }

    /**
     * GCM 加密（Base64 入参，返回 Base64 密文）。
     *
     * @param keyBase64 密钥 Base64 字符串
     * @param plaintext 明文
     * @return Base64 密文（IV + 密文 + GCM 标签）
     */
    public static String encryptGcmBase64(String keyBase64, String plaintext) {
        Assert.notNull(keyBase64, "keyBase64 must not be null");
        Assert.notNull(plaintext, "plaintext must not be null");
        byte[] key = Base64.getDecoder().decode(keyBase64);
        return Base64.getEncoder().encodeToString(encryptGcm(key, plaintext.getBytes(UTF_8)));
    }

    /**
     * GCM 解密（byte[] 入参）。
     * <p>要求密文前 12 字节为加密时生成的 IV。</p>
     *
     * @param key           密钥（16 字节）
     * @param encryptedData 密文（IV + 密文 + GCM 标签）
     * @return 明文字节
     * @throws RuntimeException 解密失败（如密钥错误或密文被篡改）
     */
    public static byte[] decryptGcm(byte[] key, byte[] encryptedData) {
        requireKey(key);
        Assert.notNull(encryptedData, "encryptedData must not be null");
        if (encryptedData.length <= GCM_IV_LENGTH) {
            throw new IllegalArgumentException("encryptedData is too short");
        }
        try {
            var iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(encryptedData, 0, iv, 0, GCM_IV_LENGTH);

            var cipher = Cipher.getInstance(TRANSFORMATION_GCM, "BC");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM_NAME), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return cipher.doFinal(encryptedData, GCM_IV_LENGTH, encryptedData.length - GCM_IV_LENGTH);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to decrypt with SM4/GCM: " + e.getMessage(), e);
        }
    }

    /**
     * GCM 解密（Hex 密文 → 明文字符串）。
     *
     * @param keyHex    密钥 Hex 字符串
     * @param cipherHex Hex 密文（IV + 密文 + GCM 标签）
     * @return 明文字符串
     */
    public static String decryptGcmHex(String keyHex, String cipherHex) {
        Assert.notNull(keyHex, "keyHex must not be null");
        Assert.notNull(cipherHex, "cipherHex must not be null");
        byte[] plainBytes = decryptGcm(HexUtils.decodeToBytes(keyHex), HexUtils.decodeToBytes(cipherHex));
        return new String(plainBytes, UTF_8);
    }

    /**
     * GCM 解密（Base64 密文 → 明文字符串）。
     *
     * @param keyBase64    密钥 Base64 字符串
     * @param cipherBase64 Base64 密文（IV + 密文 + GCM 标签）
     * @return 明文字符串
     */
    public static String decryptGcmBase64(String keyBase64, String cipherBase64) {
        Assert.notNull(keyBase64, "keyBase64 must not be null");
        Assert.notNull(cipherBase64, "cipherBase64 must not be null");
        byte[] key = Base64.getDecoder().decode(keyBase64);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherBase64);
        byte[] plainBytes = decryptGcm(key, cipherBytes);
        return new String(plainBytes, UTF_8);
    }

    // ------

    private static byte[] doCipher(byte[] key, byte @Nullable [] iv, byte[] data, String transformation, int mode) {
        requireKey(key);
        Assert.notNull(data, "data must not be null");
        if (iv != null && iv.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("IV must be 16 bytes, current length: " + iv.length);
        }
        try {
            Cipher cipher = Cipher.getInstance(transformation, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM_NAME);
            if (iv != null) {
                cipher.init(mode, keySpec, new IvParameterSpec(iv));
            } else {
                cipher.init(mode, keySpec);
            }
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("SM4 " + (mode == Cipher.ENCRYPT_MODE ? "encryption" : "decryption") + " failed: " + e.getMessage(), e);
        }
    }

    private static byte[] requireKey(byte[] key) {
        if (key == null || key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("SM4 key must be 16 bytes (128 bits), current length: " +
                    (key == null ? "null" : key.length));
        }
        return key;
    }
}

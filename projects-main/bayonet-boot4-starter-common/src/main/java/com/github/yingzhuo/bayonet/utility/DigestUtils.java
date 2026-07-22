package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 摘要算法工具类。
 *
 * <p>提供常见消息摘要算法的计算，每种算法支持 {@code byte[]} 原始结果和 {@link String} → hex 两种入口。</p>
 *
 * <p>支持的算法：</p>
 * <ul>
 *   <li>{@link #ALG_MD2 MD2} — <b>不推荐</b>，已存在已知碰撞攻击</li>
 *   <li>{@link #ALG_MD5 MD5} — <b>不推荐</b>，仅适用于非安全场景（如数据分片校验）</li>
 *   <li>{@link #ALG_SHA_1 SHA-1} — <b>不推荐</b>，已存在已知碰撞攻击</li>
 *   <li>{@link #ALG_SHA_256 SHA-256} — 推荐</li>
 *   <li>{@link #ALG_SHA_384 SHA-384} — 推荐</li>
 *   <li>{@link #ALG_SHA_512 SHA-512} — 推荐</li>
 * </ul>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * byte[] digest = DigestUtils.sha256("hello".getBytes(StandardCharsets.UTF_8));
 * String hex = DigestUtils.sha256Hex("hello");
 * }</pre>
 *
 * @author 应卓
 * @see MessageDigest
 * @see HexUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DigestUtils {

    /**
     * MD2 算法名称常量。
     * <p><b>安全警告：</b>MD2 已破解，仅建议在兼容遗留系统时使用。</p>
     */
    public static final String ALG_MD2 = "MD2";

    /**
     * MD5 算法名称常量。
     * <p><b>安全警告：</b>MD5 已破解，仅建议在非安全场景（如数据分片校验）中使用。</p>
     */
    public static final String ALG_MD5 = "MD5";

    /**
     * SHA-1 算法名称常量。
     * <p><b>安全警告：</b>SHA-1 已存在已知碰撞攻击，仅建议在兼容遗留系统时使用。</p>
     */
    public static final String ALG_SHA_1 = "SHA-1";

    /**
     * SHA-256 算法名称常量。
     */
    public static final String ALG_SHA_256 = "SHA-256";

    /**
     * SHA-384 算法名称常量。
     */
    public static final String ALG_SHA_384 = "SHA-384";

    /**
     * SHA-512 算法名称常量。
     */
    public static final String ALG_SHA_512 = "SHA-512";

    /**
     * MD2 摘要计算。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return 摘要字节数组
     * @deprecated MD2 已破解，存在已知碰撞攻击
     */
    @Deprecated
    public static byte[] md2(byte[] data) {
        return digest(ALG_MD2, data);
    }

    /**
     * MD2 摘要计算（hex 结果）。
     *
     * @param data 输入字符串，不能为 {@code null}
     * @return 十六进制摘要字符串
     * @deprecated MD2 已破解，存在已知碰撞攻击
     */
    @Deprecated
    public static String md2Hex(String data) {
        return digestHex(ALG_MD2, data);
    }

    /**
     * MD5 摘要计算。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return 摘要字节数组
     * @deprecated MD5 已破解，仅适用于非安全场景
     */
    @Deprecated
    public static byte[] md5(byte[] data) {
        return digest(ALG_MD5, data);
    }

    /**
     * MD5 摘要计算（hex 结果）。
     *
     * @param data 输入字符串，不能为 {@code null}
     * @return 十六进制摘要字符串
     * @deprecated MD5 已破解，仅适用于非安全场景
     */
    @Deprecated
    public static String md5Hex(String data) {
        return digestHex(ALG_MD5, data);
    }

    /**
     * SHA-1 摘要计算。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return 摘要字节数组
     * @deprecated SHA-1 已存在已知碰撞攻击
     */
    @Deprecated
    public static byte[] sha1(byte[] data) {
        return digest(ALG_SHA_1, data);
    }

    /**
     * SHA-1 摘要计算（hex 结果）。
     *
     * @param data 输入字符串，不能为 {@code null}
     * @return 十六进制摘要字符串
     * @deprecated SHA-1 已存在已知碰撞攻击
     */
    @Deprecated
    public static String sha1Hex(String data) {
        return digestHex(ALG_SHA_1, data);
    }

    /**
     * SHA-256 摘要计算。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return 摘要字节数组
     */
    public static byte[] sha256(byte[] data) {
        return digest(ALG_SHA_256, data);
    }

    /**
     * SHA-256 摘要计算（hex 结果）。
     *
     * @param data 输入字符串，不能为 {@code null}
     * @return 十六进制摘要字符串
     */
    public static String sha256Hex(String data) {
        return digestHex(ALG_SHA_256, data);
    }

    /**
     * SHA-384 摘要计算。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return 摘要字节数组
     */
    public static byte[] sha384(byte[] data) {
        return digest(ALG_SHA_384, data);
    }

    /**
     * SHA-384 摘要计算（hex 结果）。
     *
     * @param data 输入字符串，不能为 {@code null}
     * @return 十六进制摘要字符串
     */
    public static String sha384Hex(String data) {
        return digestHex(ALG_SHA_384, data);
    }

    /**
     * SHA-512 摘要计算。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return 摘要字节数组
     */
    public static byte[] sha512(byte[] data) {
        return digest(ALG_SHA_512, data);
    }

    /**
     * SHA-512 摘要计算（hex 结果）。
     *
     * @param data 输入字符串，不能为 {@code null}
     * @return 十六进制摘要字符串
     */
    public static String sha512Hex(String data) {
        return digestHex(ALG_SHA_512, data);
    }

    // ------

    private static byte[] digest(String algorithm, byte @Nullable [] data) {
        Assert.notNull(data, "data must not be null");
        try {
            return MessageDigest.getInstance(algorithm).digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static String digestHex(String algorithm, @Nullable String data) {
        Assert.notNull(data, "data must not be null");
        return HexUtils.encodeToString(digest(algorithm, data.getBytes(UTF_8)));
    }

}

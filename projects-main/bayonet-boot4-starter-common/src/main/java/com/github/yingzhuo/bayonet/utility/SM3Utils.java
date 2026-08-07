package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * SM3 摘要算法工具类。
 *
 * <p>SM3 为国产商用密码杂凑算法，摘要长度为 256 位（32 字节），基于
 * BouncyCastle 的 {@link SM3Digest} 实现。</p>
 *
 * <p><b>与 {@link DigestUtils} 的关系：</b>本类<b>不</b>合并进 {@link DigestUtils}，
 * 而是独立成类。理由如下：</p>
 * <ul>
 *   <li>{@link DigestUtils} 基于 JDK 内置的 {@link java.security.MessageDigest MessageDigest}，
 *       仅支持 MD5、SHA-1、SHA-256 等算法，不依赖任何第三方库；</li>
 *   <li>SM3 为国密算法，JDK 不内置，必须依赖 BouncyCastle（{@code bcprov-jdk18on}）；
 *       而 BouncyCastle 的 jar 体积较大，本模块仅以 {@code compileOnly} 方式提供，由使用者按需自行引入；</li>
 *   <li>若将 SM3 并入 {@link DigestUtils}，会使其被迫携带 BouncyCastle 依赖，
 *       破坏其"零第三方依赖"的定位。</li>
 * </ul>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * byte[] digest = SM3Utils.digest("hello".getBytes(StandardCharsets.UTF_8));
 * String hex = SM3Utils.digestHex("hello");
 * }</pre>
 *
 * @author 应卓
 * @see DigestUtils
 * @see SM3Digest
 * @see HexUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SM3Utils {

    /**
     * SM3 摘要计算。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return SM3 摘要字节数组（32 字节）
     * @throws IllegalArgumentException 若 {@code data} 为 {@code null}
     */
    public static byte[] digest(byte[] data) {
        Assert.notNull(data, "data must not be null");
        var digest = new SM3Digest();
        digest.update(data, 0, data.length);
        byte[] out = new byte[digest.getDigestSize()]; // 32 字节
        digest.doFinal(out, 0);
        return out;
    }

    /**
     * SM3 摘要计算（{@code String} 按 UTF_8 编码）。
     *
     * @param text 输入字符串，不能为 {@code null}
     * @return SM3 摘要字节数组（32 字节）
     * @throws IllegalArgumentException 若 {@code text} 为 {@code null}
     */
    public static byte[] digest(String text) {
        Assert.notNull(text, "text must not be null");
        return digest(text.getBytes(UTF_8));
    }

    /**
     * SM3 摘要计算（hex 结果）。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return 十六进制摘要字符串（64 字符，小写）
     * @throws IllegalArgumentException 若 {@code data} 为 {@code null}
     */
    public static String digestHex(byte[] data) {
        return HexUtils.encodeToString(digest(data));
    }

    /**
     * SM3 摘要计算（hex 结果，{@code String} 按 UTF_8 编码）。
     *
     * @param text 输入字符串，不能为 {@code null}
     * @return 十六进制摘要字符串（64 字符，小写）
     * @throws IllegalArgumentException 若 {@code text} 为 {@code null}
     */
    public static String digestHex(String text) {
        return HexUtils.encodeToString(digest(text));
    }

    /**
     * SM3 摘要计算（流式）。
     * <p>从输入流中读取全部数据并计算摘要，适用于文件等无法整体载入内存的场景。
     * 读取完成后不会关闭输入流。</p>
     *
     * @param stream 输入流，不能为 {@code null}
     * @return SM3 摘要字节数组（32 字节）
     * @throws UncheckedIOException 读取输入流失败
     */
    public static byte[] digestStream(InputStream stream) {
        Assert.notNull(stream, "stream must not be null");
        try {
            var digest = new SM3Digest();
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] out = new byte[digest.getDigestSize()];
            digest.doFinal(out, 0);
            return out;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

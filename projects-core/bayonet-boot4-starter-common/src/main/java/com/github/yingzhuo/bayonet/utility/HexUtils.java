package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.HexFormat;

/**
 * 十六进制编码/解码工具类。
 * <p>基于 Java 17 {@link HexFormat} 实现。</p>
 *
 * <pre>{@code
 * String hex = HexUtils.encodeToString(bytes);
 * byte[] data = HexUtils.decodeToBytes(hex);
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HexUtils {

    private static final HexFormat FORMAT = HexFormat.of(); // thread-safe

    /**
     * 将字节数组编码为十六进制字符串。
     *
     * @param bytes 输入字节数组
     * @return 十六进制字符串（小写）
     * @throws IllegalArgumentException 若 {@code bytes} 为 {@code null}
     */
    public static String encodeToString(byte[] bytes) {
        Assert.notNull(bytes, "bytes must not be null");
        return FORMAT.formatHex(bytes);
    }

    /**
     * 将十六进制字符串解码为字节数组。
     *
     * @param hexString 十六进制字符串
     * @return 解码后的字节数组
     * @throws IllegalArgumentException 若 {@code hexString} 为 {@code null} 或格式非法
     */
    public static byte[] decodeToBytes(String hexString) {
        Assert.notNull(hexString, "hexString must not be null");
        return FORMAT.parseHex(hexString);
    }

}

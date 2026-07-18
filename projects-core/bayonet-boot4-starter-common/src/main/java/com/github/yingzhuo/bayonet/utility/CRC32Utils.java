package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.zip.CRC32;

/**
 * CRC32 校验和工具类。
 * <p>提供便捷的 CRC32 值计算与十六进制字符串转换。</p>
 *
 * <pre>{@code
 * long crc = CRC32Utils.crc32Value(data);
 * String hex = CRC32Utils.crc32Hex(data);
 * }</pre>
 * @author 应卓
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CRC32Utils {

    /**
     * 计算数据的 CRC32 校验和。
     *
     * @param data 输入数据
     * @return CRC32 校验和（32 位无符号整数）
     * @throws IllegalArgumentException 若 {@code data} 为 {@code null}
     */
    public static long crc32Value(byte[] data) {
        Assert.notNull(data, "data must not be null");

        var crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    /**
     * 计算数据的 CRC32 校验和，返回固定 8 位十六进制字符串。
     *
     * @param data 输入数据
     * @return CRC32 校验和的十六进制字符串（固定 8 位，含前导零）
     * @throws IllegalArgumentException 若 {@code data} 为 {@code null}
     */
    public static String crc32Hex(byte[] data) {
        var bytes = crc32Value(data);
        return String.format("%08x", bytes);
    }

}

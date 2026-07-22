package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Base64;

/**
 * Base64 编解码工具类。
 *
 * <p>基于 JDK {@link Base64} 实现，支持标准 URL 安全模式及是否移除填充字符。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * byte[] encoded = Base64Utils.encode("hello".getBytes());
 * byte[] decoded = Base64Utils.decode(encoded);
 * byte[] urlSafe = Base64Utils.encode("hello".getBytes(), true, true);
 * }</pre>
 *
 * @author 应卓
 * @see Base64
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Base64Utils {

    /**
     * 编码（默认无填充、URL 安全）。
     *
     * @param data 输入数据，不能为 {@code null}
     * @return Base64 编码后的字节数组
     */
    public static byte[] encode(byte[] data) {
        return encode(data, true, true);
    }

    /**
     * 编码（指定是否无填充，URL 安全）。
     *
     * @param data           输入数据，不能为 {@code null}
     * @param withoutPadding 是否移除末尾的 {@code =} 填充字符
     * @return Base64 编码后的字节数组
     */
    public static byte[] encode(byte[] data, boolean withoutPadding) {
        return encode(data, withoutPadding, true);
    }

    /**
     * 编码（完全自定义）。
     *
     * @param data           输入数据，不能为 {@code null}
     * @param withoutPadding 是否移除末尾的 {@code =} 填充字符
     * @param urlSafe        是否使用 URL 安全模式（将 {@code +} 和 {@code /} 替换为 {@code -} 和 {@code _}）
     * @return Base64 编码后的字节数组
     */
    public static byte[] encode(byte[] data, boolean withoutPadding, boolean urlSafe) {
        Base64.Encoder encoder = urlSafe ? Base64.getUrlEncoder() : Base64.getEncoder();
        if (withoutPadding) {
            encoder = encoder.withoutPadding();
        }
        return encoder.encode(data);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 解码（默认 URL 安全）。
     *
     * @param data Base64 编码数据，不能为 {@code null}
     * @return 解码后的字节数组
     */
    public static byte[] decode(byte[] data) {
        return decode(data, true);
    }

    /**
     * 解码（指定是否 URL 安全）。
     *
     * @param data    Base64 编码数据，不能为 {@code null}
     * @param urlSafe 是否使用 URL 安全模式解码
     * @return 解码后的字节数组
     */
    public static byte[] decode(byte[] data, boolean urlSafe) {
        var decoder = urlSafe ? Base64.getUrlDecoder() : Base64.getDecoder();
        return decoder.decode(data);
    }

}

package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * UUID 生成工具类。
 * <p>提供基于 {@link UUID#randomUUID()} 的 UUID v4（随机 UUID）生成方法，
 * 支持标准带连字符格式和去除连字符的紧凑格式。</p>
 *
 * <pre>{@code
 * String uid = UUIDUtils.versionFourShort();  // "a1b2c3d4e5f6..."
 * String uid = UUIDUtils.versionFourLong();   // "a1b2c3d4-e5f6-..."
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UUIDUtils {

    /**
     * 生成 UUID v4 紧凑格式。
     * <p>与 {@link #versionFourLong()} 相比，去除所有连字符（{@code -}），
     * 生成 32 位十六进制字符串。适用于长度敏感的存储或展示场景。</p>
     *
     * @return 32 位无连字符 UUID 字符串
     */
    public static String versionFourShort() {
        return versionFourLong().replaceAll("-", "");
    }

    /**
     * 生成 UUID v4 标准格式。
     * <p>调用 {@link UUID#randomUUID()} 生成随机 UUID，
     * 返回标准 36 位带连字符格式（8-4-4-4-12）。</p>
     *
     * @return 36 位带连字符 UUID 字符串
     */
    public static String versionFourLong() {
        return UUID.randomUUID().toString();
    }

}

package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 字符串工具类。
 *
 * <p>提供 null 安全的字符串判空、去行空白等常用操作。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    /**
     * 获取字符串长度（null 安全）。
     *
     * @param str 字符串（可为 {@code null}）
     * @return 字符串长度，{@code null} 时返回 0
     */
    public static int length(@Nullable String str) {
        return str == null ? 0 : str.length();
    }

    /**
     * 判断字符串是否为空（null 安全）。
     *
     * @param str 字符串（可为 {@code null}）
     * @return 为 {@code null} 或空字符串时返回 {@code true}
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否非空（null 安全）。
     *
     * @param str 字符串（可为 {@code null}）
     * @return 非 {@code null} 且非空字符串时返回 {@code true}
     */
    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null 安全）。
     *
     * <p>空白指 {@code null}、空字符串或仅包含空白字符的字符串。</p>
     *
     * @param str 字符串（可为 {@code null}）
     * @return 为 {@code null}、空或仅空白时返回 {@code true}
     */
    public static boolean isBlank(@Nullable String str) {
        return str == null || str.isBlank();
    }

    /**
     * 判断字符串是否非空白（null 安全）。
     *
     * @param str 字符串（可为 {@code null}）
     * @return 非 {@code null} 且包含非空白字符时返回 {@code true}
     */
    public static boolean isNotBlank(@Nullable String str) {
        return !isBlank(str);
    }

    /**
     * 将字符串按行拆分为流（null 安全）。
     *
     * <p>支持 {@code \n}、{@code \r} 和 {@code \r\n} 换行符，由 {@link String#lines()} 实现。</p>
     *
     * @param str 字符串（可为 {@code null}）
     * @return 行流，输入为 {@code null} 时返回空流
     */
    public static Stream<String> toLines(@Nullable String str) {
        if (str == null) return Stream.empty();
        return str.lines();
    }

    /**
     * 去除字符串每行的首尾空白。
     *
     * <p>将多行字符串按行拆分，每行执行 {@link String#trim()} 后重新拼接。</p>
     *
     * @param str 字符串，不能为 {@code null}
     * @return 每行已去首尾空白的字符串
     */
    public static String trimEachLine(String str) {
        return toLines(str)
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

}

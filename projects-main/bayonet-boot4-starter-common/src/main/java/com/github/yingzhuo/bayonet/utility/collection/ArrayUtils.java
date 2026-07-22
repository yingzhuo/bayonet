package com.github.yingzhuo.bayonet.utility.collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * 数组工具类。
 * <p>提供 null-safe 的数组判空、大小获取等操作。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayUtils {

    /**
     * 判断数组是否为空（null-safe）。
     *
     * @param array 数组，可为 {@code null}
     * @param <T>   元素类型
     * @return {@code null} 或空数组返回 {@code true}
     */
    public static <T> boolean isEmpty(@Nullable T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否非空（null-safe）。
     *
     * @param array 数组，可为 {@code null}
     * @param <T>   元素类型
     * @return 非 {@code null} 且非空返回 {@code true}
     */
    public static <T> boolean isNotEmpty(@Nullable T[] array) {
        return !isEmpty(array);
    }

    /**
     * 获取数组长度（null-safe）。
     *
     * @param array 数组，可为 {@code null}
     * @param <T>   元素类型
     * @return 数组长度，{@code null} 时返回 0
     */
    public static <T> int size(@Nullable T[] array) {
        return array == null ? 0 : array.length;
    }

    /**
     * 返回数组中第一个非 null 元素。
     *
     * @param array 数组，可为 {@code null}
     * @param <T>   元素类型
     * @return 第一个非 null 元素，若数组为 {@code null}、空或全为 {@code null} 则返回 {@code null}
     */
    @Nullable
    public static <T> T firstNonNull(@Nullable T[] array) {
        if (isEmpty(array)) {
            return null;
        }
        return Arrays.stream(array)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}

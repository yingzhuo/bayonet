package com.github.yingzhuo.bayonet.utility.collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream 工厂工具类。
 * <p>提供 null-safe 的 Stream 创建方法，支持数组、{@link Collection}、{@link Iterator}、{@link Enumeration} 四种来源。</p>
 *
 * <pre>{@code
 * // 数组转 Stream（自动跳过 null 元素）
 * StreamFactories.nullSafeNewStream("a", null, "b").toList();  // [a, b]
 *
 * // Enumeration 转 Stream
 * StreamFactories.of(Collections.enumeration(List.of("a", "b")));
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamFactories {

    /**
     * 创建包含数组元素的 Stream（null-safe，自动跳过 null 元素）。
     *
     * @param elements 元素数组，可为 {@code null}
     * @param <T>      元素类型
     * @return Stream（非 {@code null}）
     */
    @SafeVarargs
    public static <T> Stream<T> nullSafeNewStream(@Nullable T... elements) {
        if (elements == null) {
            return Stream.empty();
        }
        return Arrays.stream(elements).filter(Objects::nonNull);
    }

    /**
     * 创建包含集合元素的 Stream（null-safe，自动跳过 null 元素）。
     *
     * @param elements 元素集合，可为 {@code null}
     * @param <T>      元素类型
     * @return Stream（非 {@code null}）
     */
    public static <T> Stream<T> nullSafeNewStream(@Nullable Collection<T> elements) {
        if (elements == null) {
            return Stream.empty();
        }
        return elements.stream().filter(Objects::nonNull);
    }

    // ------

    /**
     * 创建包含 {@link Iterator} 元素的 Stream（null-safe）。
     *
     * @param iterator 迭代器，可为 {@code null}
     * @param <T>      元素类型
     * @return Stream（非 {@code null}）
     */
    public static <T> Stream<T> of(@Nullable Iterator<T> iterator) {
        return of(iterator, false);
    }

    /**
     * 创建包含 {@link Iterator} 元素的 Stream（null-safe，支持并行）。
     *
     * @param iterator 迭代器，可为 {@code null}
     * @param parallel 是否并行流
     * @param <T>      元素类型
     * @return Stream（非 {@code null}）
     */
    public static <T> Stream<T> of(@Nullable Iterator<T> iterator, boolean parallel) {
        if (iterator == null) {
            return Stream.empty();
        }
        var spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, parallel);
    }

    /**
     * 创建包含 {@link Enumeration} 元素的 Stream（null-safe）。
     *
     * @param enumeration 枚举，可为 {@code null}
     * @param <T>         元素类型
     * @return Stream（非 {@code null}）
     */
    public static <T> Stream<T> of(@Nullable Enumeration<T> enumeration) {
        return of(enumeration, false);
    }

    /**
     * 创建包含 {@link Enumeration} 元素的 Stream（null-safe，支持并行）。
     *
     * @param enumeration 枚举，可为 {@code null}
     * @param parallel    是否并行流
     * @param <T>         元素类型
     * @return Stream（非 {@code null}）
     */
    public static <T> Stream<T> of(@Nullable Enumeration<T> enumeration, boolean parallel) {
        if (enumeration == null) {
            return Stream.empty();
        }
        var spliterator = Spliterators.spliteratorUnknownSize(new EnumerationIterator<>(enumeration), 0);
        return StreamSupport.stream(spliterator, parallel);
    }

}

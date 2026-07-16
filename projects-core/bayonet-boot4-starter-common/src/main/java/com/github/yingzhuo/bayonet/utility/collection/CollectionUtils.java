package com.github.yingzhuo.bayonet.utility.collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类。
 * <p>提供 null-safe 的集合大小获取、空判断、元素添加等操作。</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtils {

    /**
     * 获取集合大小（null-safe）。
     *
     * @param collection 集合，可为 {@code null}
     * @param <T>        元素类型
     * @return 集合大小，{@code null} 时返回 0
     */
    public static <T> int size(@Nullable Collection<T> collection) {
        return collection != null ? collection.size() : 0;
    }

    /**
     * 获取 Map 大小（null-safe）。
     *
     * @param map Map，可为 {@code null}
     * @param <K> 键类型
     * @param <V> 值类型
     * @return Map 大小，{@code null} 时返回 0
     */
    public static <K, V> int size(@Nullable Map<K, V> map) {
        return map != null ? map.size() : 0;
    }

    /**
     * 判断集合是否为空（null-safe）。
     *
     * @param collection 集合，可为 {@code null}
     * @param <T>        元素类型
     * @return {@code null} 或空集合返回 {@code true}
     */
    public static <T> boolean isEmpty(@Nullable Collection<T> collection) {
        return size(collection) == 0;
    }

    /**
     * 判断 Map 是否为空（null-safe）。
     *
     * @param map Map，可为 {@code null}
     * @param <K> 键类型
     * @param <V> 值类型
     * @return {@code null} 或空 Map 返回 {@code true}
     */
    public static <K, V> boolean isEmpty(@Nullable Map<K, V> map) {
        return size(map) == 0;
    }

    /**
     * 判断集合是否非空（null-safe）。
     *
     * @param collection 集合，可为 {@code null}
     * @param <T>        元素类型
     * @return 非 {@code null} 且非空返回 {@code true}
     */
    public static <T> boolean isNotEmpty(@Nullable Collection<T> collection) {
        return size(collection) != 0;
    }

    /**
     * 判断 Map 是否非空（null-safe）。
     *
     * @param map Map，可为 {@code null}
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 非 {@code null} 且非空返回 {@code true}
     */
    public static <K, V> boolean isNotEmpty(@Nullable Map<K, V> map) {
        return size(map) != 0;
    }

    /**
     * 向集合中添加一个元素（null-safe）。
     * <p>若 {@code element} 为 {@code null} 则忽略。</p>
     *
     * @param collection 集合（非 {@code null}）
     * @param element    待添加元素，可为 {@code null}
     * @param <T>        元素类型
     * @throws IllegalArgumentException 若 {@code collection} 为 {@code null}
     */
    public static <T> void nullSafeAdd(Collection<T> collection, @Nullable T element) {
        Assert.notNull(collection, "collection is required");
        if (element != null) {
            collection.add(element);
        }
    }

    /**
     * 向集合中添加数组中的全部元素（null-safe）。
     * <p>数组中的 {@code null} 元素会被忽略。</p>
     *
     * @param collection 集合（非 {@code null}）
     * @param elements   元素数组，可为 {@code null}
     * @param <T>        元素类型
     * @throws IllegalArgumentException 若 {@code collection} 为 {@code null}
     */
    public static <T> void nullSafeAddAll(Collection<T> collection, @Nullable T[] elements) {
        Assert.notNull(collection, "collection is required");
        if (elements != null) {
            for (T obj : elements) {
                if (obj != null) {
                    collection.add(obj);
                }
            }
        }
    }

    /**
     * 向集合中添加另一个集合中的全部元素（null-safe）。
     * <p>源集合中的 {@code null} 元素会被忽略。</p>
     *
     * @param collection 目标集合（非 {@code null}）
     * @param elements   源集合，可为 {@code null}
     * @param <T>        元素类型
     * @throws IllegalArgumentException 若 {@code collection} 为 {@code null}
     */
    public static <T> void nullSafeAddAll(Collection<T> collection, @Nullable Collection<T> elements) {
        Assert.notNull(collection, "collection is required");
        if (elements != null) {
            for (T obj : elements) {
                if (obj != null) {
                    collection.add(obj);
                }
            }
        }
    }

    /**
     * 向 Map 中添加另一个 Map 中的全部键值对（null-safe）。
     *
     * @param map      目标 Map（非 {@code null}）
     * @param elements 源 Map，可为 {@code null}
     * @param <K>      键类型
     * @param <V>      值类型
     * @throws IllegalArgumentException 若 {@code map} 为 {@code null}
     */
    public static <K, V> void nullSafeAddAll(Map<K, V> map, @Nullable Map<K, V> elements) {
        Assert.notNull(map, "map is required");
        if (elements != null) {
            map.putAll(elements);
        }
    }
}

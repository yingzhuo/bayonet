package com.github.yingzhuo.bayonet.utility.collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 排序工具类。
 * <p>提供数组和 List 的排序方法，默认使用 Spring 的 {@link AnnotationAwareOrderComparator}
 * （支持 {@code @Order} 和 {@code @Priority} 注解解析），
 * 也可传入自定义 {@link Comparator}。</p>
 *
 * <pre>{@code
 * // 使用默认排序
 * SortingUtils.sort(myBeanList);
 *
 * // 使用自定义比较器
 * SortingUtils.sort(myBeanList, Comparator.comparing(MyBean::getName));
 * }</pre>
 * @author 应卓
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SortingUtils {

    /**
     * 对数组进行排序（默认 {@link AnnotationAwareOrderComparator}）。
     *
     * @param array 待排序数组（非 {@code null}）
     * @param <T>   元素类型
     * @throws IllegalArgumentException 若 {@code array} 为 {@code null}
     */
    public static <T> void sort(T[] array) {
        sort(array, null);
    }

    /**
     * 对数组进行排序（使用自定义比较器）。
     * <p>若 {@code comparator} 为 {@code null}，使用默认的 {@link AnnotationAwareOrderComparator}。</p>
     *
     * @param array      待排序数组（非 {@code null}）
     * @param comparator 比较器，可为 {@code null}
     * @param <T>        元素类型
     * @throws IllegalArgumentException 若 {@code array} 为 {@code null}
     */
    public static <T> void sort(T[] array, @Nullable Comparator<? super T> comparator) {
        Assert.notNull(array, "array must not be null");
        if (comparator != null) {
            Arrays.sort(array, comparator);
        } else {
            AnnotationAwareOrderComparator.sort(array);
        }
    }

    /**
     * 对 List 进行排序（默认 {@link AnnotationAwareOrderComparator}）。
     *
     * @param list 待排序 List（非 {@code null}）
     * @param <T>  元素类型
     * @throws IllegalArgumentException 若 {@code list} 为 {@code null}
     */
    public static <T> void sort(List<T> list) {
        sort(list, null);
    }

    /**
     * 对 List 进行排序（使用自定义比较器）。
     * <p>若 {@code comparator} 为 {@code null}，使用默认的 {@link AnnotationAwareOrderComparator}。</p>
     *
     * @param list       待排序 List（非 {@code null}）
     * @param comparator 比较器，可为 {@code null}
     * @param <T>        元素类型
     * @throws IllegalArgumentException 若 {@code list} 为 {@code null}
     */
    public static <T> void sort(List<T> list, @Nullable Comparator<? super T> comparator) {
        Assert.notNull(list, "list must not be null");
        if (comparator != null) {
            list.sort(comparator);
        } else {
            AnnotationAwareOrderComparator.sort(list);
        }
    }

}

package com.github.yingzhuo.bayonet.utility.collection;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 空安全的集合构建器。
 * <p>将 {@code null} 值和 {@code null} 元素自动过滤，收集元素到目标集合类型。</p>
 *
 * <p>非线程安全。</p>
 *
 * @param <T> 元素类型
 * @author 应卓
 * @since 4.1.1
 */
public class NullSafeCollectionBuilder<T> {

    private final List<T> elements = new ArrayList<>();

    /**
     * 创建新实例。
     *
     * @param <T> 元素类型
     * @return 新构建器实例
     */
    public static <T> NullSafeCollectionBuilder<T> newInstance() {
        return new NullSafeCollectionBuilder<>();
    }

    /**
     * 私有构造器
     */
    private NullSafeCollectionBuilder() {
    }

    /**
     * 添加元素（可变参数）。
     * <p>{@code null} 值和 {@code null} 元素会被自动忽略。</p>
     *
     * @param values 要添加的元素
     * @return 当前构建器
     */
    @SafeVarargs
    public final NullSafeCollectionBuilder<T> add(@Nullable T... values) {
        if (values != null) {
            for (var v : values) {
                if (v != null) {
                    elements.add(v);
                }
            }
        }
        return this;
    }

    /**
     * 添加元素（{@link Iterable}）。
     * <p>{@code null} 值和 {@code null} 元素会被自动忽略。</p>
     *
     * @param values 要添加的元素
     * @return 当前构建器
     */
    public final NullSafeCollectionBuilder<T> add(@Nullable Iterable<T> values) {
        if (values != null) {
            for (var v : values) {
                if (v != null) {
                    elements.add(v);
                }
            }
        }
        return this;
    }

    /**
     * 添加元素（{@link Stream}）。
     * <p>{@code null} 值和 {@code null} 元素会被自动忽略。</p>
     *
     * @param values 要添加的元素
     * @return 当前构建器
     * @deprecated stream会被消费掉，这个副作用有风险
     */
    @Deprecated
    public final NullSafeCollectionBuilder<T> add(@Nullable Stream<T> values) {
        if (values != null) {
            values.filter(Objects::nonNull).forEach(elements::add);
        }
        return this;
    }

    /**
     * 以 {@link Stream} 形式返回已收集的元素。
     *
     * @return 元素流
     */
    public Stream<T> toStream() {
        return this.elements.stream();
    }

    /**
     * 返回不可变列表。
     *
     * @return 不可变列表
     */
    public List<T> toUnmodifiableList() {
        return List.copyOf(this.elements);
    }

    /**
     * 返回 {@link ArrayList}。
     *
     * @return 可变列表
     */
    public ArrayList<T> toArrayList() {
        return new ArrayList<>(this.elements);
    }

    /**
     * 返回 {@link LinkedList}。
     *
     * @return 可变列表
     */
    public LinkedList<T> toLinkedList() {
        return new LinkedList<>(this.elements);
    }

    /**
     * 返回不可变集合。
     *
     * @return 不可变集合
     */
    public Set<T> toUnmodifiableSet() {
        return this.elements.stream().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * 返回 {@link HashSet}。
     *
     * @return 可变集合
     */
    public HashSet<T> toHashSet() {
        return new HashSet<>(this.elements);
    }

    /**
     * 返回 {@link TreeSet}。
     *
     * @param comparator 比较器，为 {@code null} 时使用自然排序
     * @return 有序集合
     */
    public TreeSet<T> toTreeSet(@Nullable Comparator<? super T> comparator) {
        TreeSet<T> set;
        if (comparator != null) {
            set = new TreeSet<>(comparator);
        } else {
            set = new TreeSet<>();
        }
        set.addAll(this.elements);
        return set;
    }
}

package com.github.yingzhuo.bayonet.utility.collection.iterator;

import org.springframework.util.Assert;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * {@link Enumeration} 适配为 {@link Iterator}。
 * <p>将传统 {@code Enumeration} 接口适配为现代 {@code Iterator} 接口，
 * 便于在 for-each 循环和流式操作中使用。</p>
 *
 * <pre>{@code
 * Enumeration<String> en = ...;
 * var iterator = EnumerationIterator.newInstance(en);
 * iterator.forEachRemaining(System.out::println);
 * }</pre>
 *
 * @param <T> 元素类型
 * @author 应卓
 */
public final class EnumerationIterator<T> implements Iterator<T> {

    private final Enumeration<T> innerEnumeration;

    /**
     * 构造器。
     *
     * @param enumeration 被适配的 Enumeration（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code enumeration} 为 {@code null}
     */
    public EnumerationIterator(Enumeration<T> enumeration) {
        Assert.notNull(enumeration, "enumeration is required");
        this.innerEnumeration = enumeration;
    }

    /**
     * 创建 {@link EnumerationIterator} 实例。
     *
     * @param inner 被适配的 Enumeration（非 {@code null}）
     * @param <T>   元素类型
     * @return 新的 EnumerationIterator 实例
     * @throws IllegalArgumentException 若 {@code inner} 为 {@code null}
     */
    public static <T> EnumerationIterator<T> newInstance(Enumeration<T> inner) {
        return new EnumerationIterator<>(inner);
    }

    @Override
    public boolean hasNext() {
        return this.innerEnumeration.hasMoreElements();
    }

    @Override
    public T next() {
        return this.innerEnumeration.nextElement();
    }

}

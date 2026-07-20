package com.github.yingzhuo.bayonet.utility.collection.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 单元素迭代器。
 *
 * <p>迭代一个元素后即耗尽，适用于需要将单个元素适配为 {@link Iterator} 的场景。</p>
 *
 * <p>示例：</p>
 * <pre>{@code
 * var iterator = SingletonIterator.of("hello");
 * iterator.hasNext(); // true
 * iterator.next();    // "hello"
 * iterator.hasNext(); // false
 * }</pre>
 *
 * @param <T> 元素类型
 * @author 应卓
 * @since 4.1.0
 */
public class SingletonIterator<T> implements Iterator<T> {

    private final T element;
    private boolean consumed = false;

    /**
     * 创建单元素迭代器。
     *
     * @param element 要迭代的元素（不能为 {@code null}）
     */
    public SingletonIterator(T element) {
        this.element = element;
    }

    /**
     * 创建单元素迭代器的工厂方法。
     *
     * @param element 要迭代的元素（不能为 {@code null}）
     * @param <T>     元素类型
     * @return SingletonIterator 实例
     */
    public static <T> SingletonIterator<T> of(T element) {
        return new SingletonIterator<>(element);
    }

    @Override
    public boolean hasNext() {
        return !consumed;
    }

    @Override
    public T next() {
        if (consumed) {
            throw new NoSuchElementException();
        }
        consumed = true;
        return element;
    }

}

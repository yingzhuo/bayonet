package com.github.yingzhuo.bayonet.utility;

import org.jspecify.annotations.Nullable;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * 不可变二元组（Pair）工具类。
 * <p>提供 {@link #of(Object, Object)} 和 {@link #ofNullable(Object, Object)} 两种构造方式，
 * 以及三类取值方法：直接返回、非空断言返回、{@link Optional} 返回。</p>
 *
 * @param <A> 第一个元素类型
 * @param <B> 第二个元素类型
 * @author 应卓
 * @see Tuple
 * @see Quadruple
 * @since 4.1.1
 */
public final class Pair<A, B> implements Iterable<Object>, Serializable {

    @Serial
    private static final long serialVersionUID = 8790880950788772092L;

    private final @Nullable A first;
    private final @Nullable B second;

    private Pair(@Nullable A first, @Nullable B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * 创建可包含 {@code null} 元素的 Pair。
     *
     * @param first  第一个元素，可为 {@code null}
     * @param second 第二个元素，可为 {@code null}
     * @param <A>    第一个元素类型
     * @param <B>    第二个元素类型
     * @return Pair 实例
     */
    public static <A, B> Pair<A, B> ofNullable(@Nullable A first, @Nullable B second) {
        return new Pair<>(first, second);
    }

    /**
     * 创建不可为 {@code null} 元素的 Pair。
     *
     * @param first  第一个元素，不能为 {@code null}
     * @param second 第二个元素，不能为 {@code null}
     * @param <A>    第一个元素类型
     * @param <B>    第二个元素类型
     * @return Pair 实例
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     */
    public static <A, B> Pair<A, B> of(A first, B second) {
        Assert.notNull(first, "first element must not be null");
        Assert.notNull(second, "second element must not be null");
        return new Pair<>(first, second);
    }

    /**
     * 获取第一个元素。
     *
     * @return 第一个元素，可为 {@code null}
     */
    public @Nullable A getFirst() {
        return first;
    }

    /**
     * 获取第二个元素。
     *
     * @return 第二个元素，可为 {@code null}
     */
    public @Nullable B getSecond() {
        return second;
    }

    /**
     * 获取第一个元素，若为 {@code null} 则抛出异常。
     *
     * @return 第一个元素（非 {@code null}）
     * @throws NoSuchElementException 第一个元素为 {@code null} 时抛出
     */
    public A getRequiredFirst() {
        var first = getFirst();
        if (first == null) {
            throw new NoSuchElementException("first element is null");
        }
        return first;
    }

    /**
     * 获取第二个元素，若为 {@code null} 则抛出异常。
     *
     * @return 第二个元素（非 {@code null}）
     * @throws NoSuchElementException 第二个元素为 {@code null} 时抛出
     */
    public B getRequiredSecond() {
        var second = getSecond();
        if (second == null) {
            throw new NoSuchElementException("second element is null");
        }
        return second;
    }

    /**
     * 获取第一个元素作为 {@link Optional}。
     *
     * @return 第一个元素的 {@link Optional}
     */
    public Optional<A> getOptionalFirst() {
        return Optional.ofNullable(first);
    }

    /**
     * 获取第二个元素作为 {@link Optional}。
     *
     * @return 第二个元素的 {@link Optional}
     */
    public Optional<B> getOptionalSecond() {
        return Optional.ofNullable(second);
    }

    // ------

    @Override
    public Iterator<Object> iterator() {
        return List.of(getRequiredFirst(), getRequiredSecond()).iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("first", first)
                .append("second", second)
                .toString();
    }
}

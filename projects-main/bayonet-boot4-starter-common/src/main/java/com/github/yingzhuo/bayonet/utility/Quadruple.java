package com.github.yingzhuo.bayonet.utility;

import org.jspecify.annotations.Nullable;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * 不可变四元组（Quadruple）工具类。
 * <p>提供 {@link #of(Object, Object, Object, Object)} 和 {@link #ofNullable(Object, Object, Object, Object)} 两种构造方式，
 * 以及三类取值方法：直接返回、非空断言返回、{@link Optional} 返回。</p>
 *
 * @param <A> 第一个元素类型
 * @param <B> 第二个元素类型
 * @param <C> 第三个元素类型
 * @param <D> 第四个元素类型
 * @author 应卓
 * @see Pair
 * @see Tuple
 * @since 4.1.1
 */
public final class Quadruple<A, B, C, D> implements Iterable<Object>, Serializable {

    @Serial
    private static final long serialVersionUID = -4012895682687081549L;

    private final @Nullable A first;
    private final @Nullable B second;
    private final @Nullable C third;
    private final @Nullable D fourth;

    private Quadruple(@Nullable A first, @Nullable B second, @Nullable C third, @Nullable D fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    /**
     * 创建可包含 {@code null} 元素的 Quadruple。
     *
     * @param first  第一个元素，可为 {@code null}
     * @param second 第二个元素，可为 {@code null}
     * @param third  第三个元素，可为 {@code null}
     * @param fourth 第四个元素，可为 {@code null}
     * @param <A>    第一个元素类型
     * @param <B>    第二个元素类型
     * @param <C>    第三个元素类型
     * @param <D>    第四个元素类型
     * @return Quadruple 实例
     */
    public static <A, B, C, D> Quadruple<A, B, C, D> ofNullable(@Nullable A first, @Nullable B second, @Nullable C third, @Nullable D fourth) {
        return new Quadruple<>(first, second, third, fourth);
    }

    /**
     * 创建不可为 {@code null} 元素的 Quadruple。
     *
     * @param first  第一个元素，不能为 {@code null}
     * @param second 第二个元素，不能为 {@code null}
     * @param third  第三个元素，不能为 {@code null}
     * @param fourth 第四个元素，不能为 {@code null}
     * @param <A>    第一个元素类型
     * @param <B>    第二个元素类型
     * @param <C>    第三个元素类型
     * @param <D>    第四个元素类型
     * @return Quadruple 实例
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     */
    public static <A, B, C, D> Quadruple<A, B, C, D> of(A first, B second, C third, D fourth) {
        Assert.notNull(first, "first element must not be null");
        Assert.notNull(second, "second element must not be null");
        Assert.notNull(third, "third element must not be null");
        Assert.notNull(fourth, "fourth element must not be null");
        return new Quadruple<>(first, second, third, fourth);
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
     * 获取第三个元素。
     *
     * @return 第三个元素，可为 {@code null}
     */
    public @Nullable C getThird() {
        return third;
    }

    /**
     * 获取第四个元素。
     *
     * @return 第四个元素，可为 {@code null}
     */
    public @Nullable D getFourth() {
        return fourth;
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
     * 获取第三个元素，若为 {@code null} 则抛出异常。
     *
     * @return 第三个元素（非 {@code null}）
     * @throws NoSuchElementException 第三个元素为 {@code null} 时抛出
     */
    public C getRequiredThird() {
        var third = getThird();
        if (third == null) {
            throw new NoSuchElementException("third element is null");
        }
        return third;
    }

    /**
     * 获取第四个元素，若为 {@code null} 则抛出异常。
     *
     * @return 第四个元素（非 {@code null}）
     * @throws NoSuchElementException 第四个元素为 {@code null} 时抛出
     */
    public D getRequiredFourth() {
        var fourth = getFourth();
        if (fourth == null) {
            throw new NoSuchElementException("fourth element is null");
        }
        return fourth;
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

    /**
     * 获取第三个元素作为 {@link Optional}。
     *
     * @return 第三个元素的 {@link Optional}
     */
    public Optional<C> getOptionalThird() {
        return Optional.ofNullable(third);
    }

    /**
     * 获取第四个元素作为 {@link Optional}。
     *
     * @return 第四个元素的 {@link Optional}
     */
    public Optional<D> getOptionalFourth() {
        return Optional.ofNullable(fourth);
    }

    // ------

    @Override
    public Iterator<Object> iterator() {
        return List.of(getRequiredFirst(), getRequiredSecond(), getRequiredThird(), getRequiredFourth()).iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quadruple<?, ?, ?, ?> quadruple)) return false;
        return Objects.equals(first, quadruple.first)
                && Objects.equals(second, quadruple.second)
                && Objects.equals(third, quadruple.third)
                && Objects.equals(fourth, quadruple.fourth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("first", first)
                .append("second", second)
                .append("third", third)
                .append("fourth", fourth)
                .toString();
    }
}

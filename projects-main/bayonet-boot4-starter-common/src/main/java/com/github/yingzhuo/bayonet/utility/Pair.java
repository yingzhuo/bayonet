package com.github.yingzhuo.bayonet.utility;

import org.jspecify.annotations.Nullable;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * 不可变二元组（Pair）工具类。
 * <p>提供 {@link #of(Object, Object)} 和 {@link #ofNullable(Object, Object)} 两种构造方式，
 * 以及三类取值方法：直接返回、非空断言返回、{@link Optional} 返回。</p>
 *
 * @param <L> 左侧元素类型
 * @param <R> 右侧元素类型
 * @author 应卓
 * @see Tuple
 * @since 4.1.1
 */
public final class Pair<L, R> implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 8790880950788772092L;

    private final @Nullable L left;
    private final @Nullable R right;

    private Pair(@Nullable L left, @Nullable R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * 创建可包含 {@code null} 元素的 Pair。
     *
     * @param left  左侧元素，可为 {@code null}
     * @param right 右侧元素，可为 {@code null}
     * @param <L>   左侧元素类型
     * @param <R>   右侧元素类型
     * @return Pair 实例
     */
    public static <L, R> Pair<L, R> ofNullable(@Nullable L left, @Nullable R right) {
        return new Pair<>(left, right);
    }

    /**
     * 创建不可为 {@code null} 元素的 Pair。
     *
     * @param left  左侧元素，不能为 {@code null}
     * @param right 右侧元素，不能为 {@code null}
     * @param <L>   左侧元素类型
     * @param <R>   右侧元素类型
     * @return Pair 实例
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     */
    public static <L, R> Pair<L, R> of(L left, R right) {
        Assert.notNull(left, "left element must not be null");
        Assert.notNull(right, "right element must not be null");
        return new Pair<>(left, right);
    }

    /**
     * 获取左侧元素。
     *
     * @return 左侧元素，可为 {@code null}
     */
    public @Nullable L getLeft() {
        return left;
    }

    /**
     * 获取右侧元素。
     *
     * @return 右侧元素，可为 {@code null}
     */
    public @Nullable R getRight() {
        return right;
    }

    /**
     * 获取左侧元素，若为 {@code null} 则抛出异常。
     *
     * @return 左侧元素（非 {@code null}）
     * @throws NoSuchElementException 左侧元素为 {@code null} 时抛出
     */
    public L getRequiredLeft() {
        var left = getLeft();
        if (left == null) {
            throw new NoSuchElementException("left element is null");
        }
        return left;
    }

    /**
     * 获取右侧元素，若为 {@code null} 则抛出异常。
     *
     * @return 右侧元素（非 {@code null}）
     * @throws NoSuchElementException 右侧元素为 {@code null} 时抛出
     */
    public R getRequiredRight() {
        var right = getRight();
        if (right == null) {
            throw new NoSuchElementException("right element is null");
        }
        return right;
    }

    /**
     * 获取左侧元素作为 {@link Optional}。
     *
     * @return 左侧元素的 {@link Optional}
     */
    public Optional<L> getOptionalLeft() {
        return Optional.ofNullable(getLeft());
    }

    /**
     * 获取右侧元素作为 {@link Optional}。
     *
     * @return 右侧元素的 {@link Optional}
     */
    public Optional<R> getOptionalRight() {
        return Optional.ofNullable(getRight());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("left", left)
                .append("right", right)
                .toString();
    }
}

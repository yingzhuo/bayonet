package com.github.yingzhuo.bayonet.utility;

import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * 不可变三元组（Tuple）工具类。
 * <p>提供 {@link #of(Object, Object, Object)} 和 {@link #ofNullable(Object, Object, Object)} 两种构造方式，
 * 以及三类取值方法：直接返回、非空断言返回、{@link Optional} 返回。</p>
 *
 * @param <L> 左侧元素类型
 * @param <M> 中间元素类型
 * @param <R> 右侧元素类型
 * @author 应卓
 * @see Pair
 * @since 4.1.1
 */
public final class Tuple<L, M, R> implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 6823405761714024037L;

    private final @Nullable L left;
    private final @Nullable M middle;
    private final @Nullable R right;

    /**
     * 创建可包含 {@code null} 元素的 Tuple。
     *
     * @param left   左侧元素，可为 {@code null}
     * @param middle 中间元素，可为 {@code null}
     * @param right  右侧元素，可为 {@code null}
     * @param <L>    左侧元素类型
     * @param <M>    中间元素类型
     * @param <R>    右侧元素类型
     * @return Tuple 实例
     */
    public static <L, M, R> Tuple<L, M, R> ofNullable(@Nullable L left, @Nullable M middle, @Nullable R right) {
        return new Tuple<>(left, middle, right);
    }

    /**
     * 创建不可为 {@code null} 元素的 Tuple。
     *
     * @param left   左侧元素，不能为 {@code null}
     * @param middle 中间元素，不能为 {@code null}
     * @param right  右侧元素，不能为 {@code null}
     * @param <L>    左侧元素类型
     * @param <M>    中间元素类型
     * @param <R>    右侧元素类型
     * @return Tuple 实例
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     */
    public static <L, M, R> Tuple<L, M, R> of(L left, M middle, R right) {
        Assert.notNull(left, "left element must not be null");
        Assert.notNull(middle, "middle element must not be null");
        Assert.notNull(right, "right element must not be null");
        return new Tuple<>(left, middle, right);
    }

    private Tuple(@Nullable L left, @Nullable M middle, @Nullable R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
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
     * 获取中间元素。
     *
     * @return 中间元素，可为 {@code null}
     */
    public @Nullable M getMiddle() {
        return middle;
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
     * 获取中间元素，若为 {@code null} 则抛出异常。
     *
     * @return 中间元素（非 {@code null}）
     * @throws NoSuchElementException 中间元素为 {@code null} 时抛出
     */
    public M getRequiredMiddle() {
        var middle = getMiddle();
        if (middle == null) {
            throw new NoSuchElementException("middle element is null");
        }
        return middle;
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
     * 获取中间元素作为 {@link Optional}。
     *
     * @return 中间元素的 {@link Optional}
     */
    public Optional<M> getOptionalMiddle() {
        return Optional.ofNullable(getMiddle());
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
        if (!(obj instanceof Tuple<?, ?, ?> tuple)) return false;
        return Objects.equals(left, tuple.left)
                && Objects.equals(middle, tuple.middle)
                && Objects.equals(right, tuple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, middle, right);
    }

    @Override
    public String toString() {
        return "Tuple{left=" + left + ", middle=" + middle + ", right=" + right + "}";
    }
}

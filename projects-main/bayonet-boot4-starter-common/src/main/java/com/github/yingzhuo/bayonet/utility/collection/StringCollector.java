package com.github.yingzhuo.bayonet.utility.collection;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 空安全的字符串收集器。
 * <p>将 {@code null} 值和不符合条件的空串/空白串自动过滤，收集字符串到目标集合类型。</p>
 *
 * <p>非线程安全。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public final class StringCollector {

    private final boolean emptyAllowed;
    private final boolean blankAllowed;
    private final List<String> elements = new ArrayList<>();

    private StringCollector(boolean emptyAllowed, boolean blankAllowed) {
        this.emptyAllowed = emptyAllowed;
        this.blankAllowed = blankAllowed;
    }

    /**
     * 创建新实例。
     * <p>仅过滤 {@code null} 值，空串和空白串均保留。</p>
     *
     * @return 新收集器实例
     */
    public static StringCollector newInstance() {
        return new StringCollector(true, true);
    }

    /**
     * 创建过滤空串的实例。
     * <p>过滤 {@code null} 值和空串（{@code ""}），空白串（如 {@code "  "}）保留。</p>
     *
     * @return 新收集器实例
     */
    public static StringCollector ofNonEmpty() {
        return new StringCollector(false, true);
    }

    /**
     * 创建过滤空白串的实例。
     * <p>过滤 {@code null} 值、空串和空白串（{@code "  "}）。</p>
     *
     * @return 新收集器实例
     */
    public static StringCollector ofNonBlank() {
        return new StringCollector(false, false);
    }

    private boolean accept(@Nullable String value) {
        if (value == null) return false;
        if (!blankAllowed && value.isBlank()) return false;
        //noinspection RedundantIfStatement
        if (!emptyAllowed && value.isEmpty()) return false;
        return true;
    }

    /**
     * 添加字符串（可变参数）。
     * <p>不符合条件的字符串会被自动忽略。</p>
     *
     * @param values 要添加的字符串
     * @return 当前收集器
     */
    @SafeVarargs
    public final StringCollector add(@Nullable String... values) {
        if (values != null) {
            for (var v : values) {
                if (accept(v)) {
                    elements.add(v);
                }
            }
        }
        return this;
    }

    /**
     * 添加字符串（{@link Iterable}）。
     * <p>不符合条件的字符串会被自动忽略。</p>
     *
     * @param values 要添加的字符串
     * @return 当前收集器
     */
    public StringCollector add(@Nullable Iterable<String> values) {
        if (values != null) {
            for (var v : values) {
                if (accept(v)) {
                    elements.add(v);
                }
            }
        }
        return this;
    }

    /**
     * 添加字符串（{@link Stream}）。
     * <p>不符合条件的字符串会被自动忽略。</p>
     *
     * @param values 要添加的字符串
     * @return 当前收集器
     */
    public StringCollector add(@Nullable Stream<String> values) {
        if (values != null) {
            values.filter(this::accept).forEach(elements::add);
        }
        return this;
    }

    /**
     * 以 {@link Stream} 形式返回已收集的字符串。
     *
     * @return 字符串流
     */
    public Stream<String> toStream() {
        return elements.stream();
    }

    /**
     * 返回不可变列表。
     *
     * @return 不可变列表
     */
    public List<String> toUnmodifiableList() {
        return List.copyOf(elements);
    }

    /**
     * 返回 {@link ArrayList}。
     *
     * @return 可变列表
     */
    public ArrayList<String> toArrayList() {
        return new ArrayList<>(elements);
    }

    /**
     * 返回 {@link LinkedList}。
     *
     * @return 可变列表
     */
    public LinkedList<String> toLinkedList() {
        return new LinkedList<>(elements);
    }

    /**
     * 返回不可变集合。
     *
     * @return 不可变集合
     */
    public Set<String> toUnmodifiableSet() {
        return elements.stream().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * 返回 {@link HashSet}。
     *
     * @return 可变集合
     */
    public HashSet<String> toHashSet() {
        return new HashSet<>(elements);
    }

    /**
     * 返回 {@link TreeSet}（自然排序）。
     *
     * @return 有序集合
     */
    public TreeSet<String> toTreeSet() {
        return toTreeSet(null);
    }

    /**
     * 返回 {@link TreeSet}。
     *
     * @param comparator 比较器，为 {@code null} 时使用自然排序
     * @return 有序集合
     */
    public TreeSet<String> toTreeSet(@Nullable Comparator<? super String> comparator) {
        var set = new TreeSet<String>(Objects.requireNonNullElseGet(comparator, Comparator::naturalOrder));
        set.addAll(elements);
        return set;
    }

    /**
     * 以指定分隔符拼接字符串。
     *
     * @param delimiter 分隔符
     * @return 拼接后的字符串
     */
    public String join(CharSequence delimiter) {
        return String.join(delimiter, elements);
    }

    /**
     * 以指定分隔符和前后缀拼接字符串。
     *
     * @param delimiter 分隔符
     * @param prefix    前缀
     * @param suffix    后缀
     * @return 拼接后的字符串
     */
    public String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return elements.stream().collect(Collectors.joining(delimiter, prefix, suffix));
    }
}

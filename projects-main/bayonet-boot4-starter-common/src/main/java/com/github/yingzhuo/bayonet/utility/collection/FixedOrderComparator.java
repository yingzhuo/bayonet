package com.github.yingzhuo.bayonet.utility.collection;

import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 固定顺序比较器。
 *
 * <p>按照指定的元素列表顺序进行排序，列表中未出现的元素根据 {@link #greaterIfMissing} 决定排在末尾或开头。
 * 适用于需要按自定义枚举或固定优先级排序的场景。</p>
 *
 * <p>示例：</p>
 * <pre>{@code
 * var comparator = new FixedOrderComparator<>("低", "中", "高");
 * // 排序结果: "低" < "中" < "高"，其他值排末尾
 * }</pre>
 *
 * @param <T> 元素类型
 * @author 应卓
 * @since 4.1.0
 */
public class FixedOrderComparator<T> implements Comparator<T> {

    private final boolean greaterIfMissing;
    private final Object[] array;

    /**
     * 创建固定顺序比较器。
     * <p>缺失元素排在末尾（即大于所有固定元素）。</p>
     *
     * @param objs 固定顺序列表
     */
    public FixedOrderComparator(List<T> objs) {
        this(true, objs);
    }

    /**
     * 创建固定顺序比较器。
     *
     * @param greaterIfMissing 缺失元素是否排在末尾（{@code true} 排末尾，{@code false} 排开头）
     * @param objs             固定顺序列表
     */
    public FixedOrderComparator(boolean greaterIfMissing, List<T> objs) {
        Assert.notEmpty(objs, "objs is empty");
        this.greaterIfMissing = greaterIfMissing;
        this.array = objs.toArray(new Object[0]);
    }

    /**
     * 创建固定顺序比较器。
     * <p>缺失元素排在末尾（即大于所有固定元素）。</p>
     *
     * @param objs 固定顺序元素
     */
    @SafeVarargs
    public FixedOrderComparator(T... objs) {
        this(true, objs);
    }

    /**
     * 创建固定顺序比较器。
     *
     * @param greaterIfMissing 缺失元素是否排在末尾（{@code true} 排末尾，{@code false} 排开头）
     * @param objs             固定顺序元素
     */
    @SafeVarargs
    public FixedOrderComparator(boolean greaterIfMissing, T... objs) {
        Assert.notEmpty(objs, "objs is empty");
        this.greaterIfMissing = greaterIfMissing;
        this.array = objs.clone();
    }

    @Override
    public int compare(T o1, T o2) {
        final int index1 = getOrder(o1);
        final int index2 = getOrder(o2);
        return Integer.compare(index1, index2);
    }

    private int getOrder(T object) {
        int order = -1;

        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], object)) {
                order = i;
                break;
            }
        }

        if (order < 0) {
            order = this.greaterIfMissing ? this.array.length : -1;
        }
        return order;
    }

}

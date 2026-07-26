package com.github.yingzhuo.bayonet.function;

/**
 * 命名接口，提供名称标识。
 *
 * <p>实现此接口的类可以关联一个逻辑名称，用于在多实例场景中按名称区分。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public interface Named {

    /**
     * 获取名称。
     *
     * @return 名称（非 {@code null}）
     */
    String getName();

}

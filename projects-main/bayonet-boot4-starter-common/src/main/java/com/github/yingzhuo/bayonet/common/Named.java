package com.github.yingzhuo.bayonet.common;

import org.jspecify.annotations.Nullable;

/**
 * 具名对象。
 * <p>实现该接口的对象具有一个名称。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public interface Named {

    /**
     * 获取名称。
     *
     * @return 名称，可能为 {@code null}
     */
    @Nullable
    String getName();

}

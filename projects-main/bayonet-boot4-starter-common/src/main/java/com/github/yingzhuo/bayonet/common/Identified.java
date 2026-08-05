package com.github.yingzhuo.bayonet.common;

import org.jspecify.annotations.Nullable;

/**
 * 具 ID 对象。
 * <p>实现该接口的对象具有一个唯一标识。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public interface Identified {

    /**
     * 获取唯一标识。
     *
     * @return 唯一标识，可能为 {@code null}
     */
    @Nullable
    String getId();

}

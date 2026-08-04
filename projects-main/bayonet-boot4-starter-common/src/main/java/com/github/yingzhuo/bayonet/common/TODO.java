package com.github.yingzhuo.bayonet.common;

import org.jetbrains.annotations.ApiStatus;

/**
 * 占位用暂未实现异常
 * <p>主要让 AI Agent识别，不作其他用途。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@ApiStatus.Internal
public final class TODO extends UnsupportedOperationException {

    public static TODO newInstance() {
        return new TODO();
    }
}

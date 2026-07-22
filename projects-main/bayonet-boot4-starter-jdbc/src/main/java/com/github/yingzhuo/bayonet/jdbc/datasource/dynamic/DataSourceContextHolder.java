package com.github.yingzhuo.bayonet.jdbc.datasource.dynamic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

/**
 * 数据源上下文持有者。
 *
 * <p>基于 {@link ThreadLocal} 存储当前线程的数据源标识符，供 {@link DynamicDataSource} 路由使用。
 * 使用后必须调用 {@link #clear()} 清理，防止内存泄漏。</p>
 *
 * @author 应卓
 * @see DynamicDataSource
 * @see DataSourceSwitchingAspect
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataSourceContextHolder {

    private static final ThreadLocal<@Nullable String> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程的数据源标识符。
     *
     * @param name 数据源标识符
     */
    public static void set(String name) {
        Assert.hasText(name, "name must not be empty");
        CONTEXT.set(name);
    }

    /**
     * 获取当前线程的数据源标识符。
     *
     * @return 数据源标识符，未设置时返回 {@code null}
     */
    @Nullable
    public static String get() {
        return CONTEXT.get();
    }

    /**
     * 清理当前线程的数据源标识符，防止内存泄漏。
     */
    public static void clear() {
        CONTEXT.remove();
    }
}

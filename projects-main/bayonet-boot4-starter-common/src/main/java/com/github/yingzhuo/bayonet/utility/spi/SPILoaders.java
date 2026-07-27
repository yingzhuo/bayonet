package com.github.yingzhuo.bayonet.utility.spi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * SPI 加载器门面工具类。
 *
 * <p>统一合并 {@link SpringFactoriesUtils} 和 {@link ServiceLoaderUtils} 的加载结果，
 * 优先返回 Spring Factories 中注册的实现，再追加 Java {@link java.util.ServiceLoader} 中的实现。</p>
 *
 * @author 应卓
 * @see SpringFactoriesUtils
 * @see ServiceLoaderUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SPILoaders {

    /**
     * 加载指定 SPI 接口的所有实现。
     * <p>合并顺序：Spring Factories → ServiceLoader。返回不可变列表。</p>
     *
     * @param targetType SPI 接口类型
     * @param <T>        SPI 接口类型
     * @return 所有 SPI 实现实例的不可变列表（非 {@code null}）
     */
    public static <T> List<T> load(Class<T> targetType) {
        Assert.notNull(targetType, "targetType must not be null");

        if (targetType.isPrimitive()) {
            return List.of();
        }

        var list = new ArrayList<T>();
        list.addAll(SpringFactoriesUtils.load(targetType));
        list.addAll(ServiceLoaderUtils.load(targetType));
        return Collections.unmodifiableList(list);
    }

    /**
     * 加载指定 SPI 接口的第一个实现。
     *
     * @param targetType SPI 接口类型
     * @param <T>        SPI 接口类型
     * @return 第一个 SPI 实现实例的 {@link Optional}，若无实现则返回 {@link Optional#empty()}
     */
    public static <T> Optional<T> loadFirst(Class<T> targetType) {
        var list = load(targetType);
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

}

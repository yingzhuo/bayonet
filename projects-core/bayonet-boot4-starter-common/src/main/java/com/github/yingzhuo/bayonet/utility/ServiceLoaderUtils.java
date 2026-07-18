package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Stream;

/**
 * {@link ServiceLoader} 工具类
 * <p>提供流式 API，按类型加载 SPI 服务实现。</p>
 *
 * <pre>{@code
 * Stream<MyService> services = ServiceLoaderUtils.load(MyService.class);
 * }</pre>
 *
 * @see SpringFactoriesUtils
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceLoaderUtils {

    /**
     * 加载指定 SPI 类型的所有服务实现
     *
     * @param targetType SPI 服务类型
     * @param <T>        SPI 服务类型
     * @return 服务实现实例的流，不会为 {@code null}
     * @throws IllegalArgumentException 若 {@code targetType} 为 {@code null}
     */
    public static <T> Stream<T> load(Class<T> targetType) {
        return load(targetType, null);
    }

    /**
     * 加载指定 SPI 类型的所有服务实现（自定义类加载器）
     *
     * @param targetType  SPI 服务类型
     * @param classLoader 类加载器，{@code null} 时使用默认类加载器
     * @param <T>         SPI 服务类型
     * @return 服务实现实例的流，不会为 {@code null}
     * @throws IllegalArgumentException 若 {@code targetType} 为 {@code null}
     */
    public static <T> Stream<T> load(Class<T> targetType, @Nullable ClassLoader classLoader) {
        Assert.notNull(targetType, "targetType must not be null");

        classLoader = Objects.requireNonNullElseGet(classLoader, ClassUtils::getDefaultClassLoader);
        return ServiceLoader.load(targetType, classLoader)
                .stream()
                .map(ServiceLoader.Provider::get);
    }

}

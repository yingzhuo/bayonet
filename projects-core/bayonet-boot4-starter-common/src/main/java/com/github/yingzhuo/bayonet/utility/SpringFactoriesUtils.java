package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

/**
 * {@link SpringFactoriesLoader} 工具类
 * <p>提供流式 API，支持按类型加载工厂实现、指定资源位置、自定义类加载器和实现类过滤器。</p>
 *
 * <pre>{@code
 * // 默认加载
 * Stream<MyInterface> all = SpringFactoriesUtils.load(MyInterface.class);
 * }</pre>
 *
 * @see ServiceLoaderUtils
 * @author 应卓
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpringFactoriesUtils {

    /**
     * 加载指定类型的所有工厂实现
     *
     * @param targetType 工厂目标类型
     * @param <T>        工厂目标类型
     * @return 工厂实现实例的流，不会为 {@code null}
     * @throws IllegalArgumentException 若 {@code targetType} 为 {@code null}
     */
    public static <T> Stream<T> load(Class<T> targetType) {
        return load(targetType, null, null);
    }

    /**
     * 加载指定类型的所有工厂实现（自定义 factories 资源位置）
     *
     * @param targetType                      工厂目标类型
     * @param springFactoriesResourceLocation SpringFactories 资源位置，{@code null} 时使用默认位置
     * @param <T>                             工厂目标类型
     * @return 工厂实现实例的流，不会为 {@code null}
     * @throws IllegalArgumentException 若 {@code targetType} 为 {@code null}
     */
    public static <T> Stream<T> load(Class<T> targetType, @Nullable String springFactoriesResourceLocation) {
        return load(targetType, springFactoriesResourceLocation, null);
    }

    /**
     * 加载指定类型的所有工厂实现（自定义资源位置和类加载器）
     *
     * @param targetType                      工厂目标类型
     * @param springFactoriesResourceLocation SpringFactories 资源位置，{@code null} 时使用默认位置
     * @param classLoader                     类加载器，{@code null} 时使用默认类加载器
     * @param <T>                             工厂目标类型
     * @return 工厂实现实例的流，不会为 {@code null}
     * @throws IllegalArgumentException 若 {@code targetType} 为 {@code null}
     */
    public static <T> Stream<T> load(Class<T> targetType, @Nullable String springFactoriesResourceLocation, @Nullable ClassLoader classLoader) {
        Assert.notNull(targetType, "targetType must not be null");

        if (!StringUtils.hasText(springFactoriesResourceLocation)) {
            springFactoriesResourceLocation = "META-INF/spring.factories";
        }

        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }

        return SpringFactoriesLoader.forResourceLocation(springFactoriesResourceLocation, classLoader)
                .load(targetType)
                .stream();
    }
}

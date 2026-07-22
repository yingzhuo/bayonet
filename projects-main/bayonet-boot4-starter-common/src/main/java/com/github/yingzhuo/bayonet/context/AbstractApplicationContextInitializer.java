package com.github.yingzhuo.bayonet.context;

import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * {@link ApplicationContextInitializer} 抽象基类。
 *
 * <p>提供从路径加载资源文件的基础能力，子类只需实现 {@link #initialize(ConfigurableApplicationContext)}
 * 并按需调用 {@link #loadResource(ResourceLoader, String)} 获取资源。</p>
 *
 * @param <T> 应用上下文类型
 * @author 应卓
 * @see PropertiesLoadingInitializer
 * @see com.github.yingzhuo.bayonet.hocon.context.HoconLoadingInitializer
 * @since 4.1.0
 */
public abstract class AbstractApplicationContextInitializer<T extends ConfigurableApplicationContext>
        implements ApplicationContextInitializer<T> {

    /**
     * 从指定路径加载资源文件。
     * <p>仅当资源存在且可读时返回非 null 的 {@link Resource}，否则返回 {@code null}。</p>
     *
     * @param resourceLoader 资源加载器，通常为 {@link ConfigurableApplicationContext} 本身
     * @param location       资源路径（支持 {@code file:}、{@code classpath:} 等前缀）
     * @return 可读的 {@link Resource}，不存在或不可读时返回 {@code null}
     */
    protected final @Nullable Resource loadResource(ResourceLoader resourceLoader, String location) {
        var resource = resourceLoader.getResource(location);
        return resource.exists() && resource.isReadable() ? resource : null;
    }

}

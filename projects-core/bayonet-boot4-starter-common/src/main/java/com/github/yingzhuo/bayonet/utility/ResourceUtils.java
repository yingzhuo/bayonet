package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.io.ApplicationResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 资源加载工具类。
 * <p>提供便捷的资源读取方法，优先使用 Spring 上下文中的 {@link ResourceLoader}，
 * 不可用时回退到 {@link ApplicationResourceLoader}。</p>
 *
 * <pre>{@code
 * byte[] data = ResourceUtils.loadBytes("classpath:config.properties");
 * String text = ResourceUtils.loadText("file:/tmp/app.log", StandardCharsets.UTF_8);
 * }</pre>
 * @author 应卓
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceUtils {

    /**
     * 获取 {@link ResourceLoader}。
     * <p>优先从 Spring 上下文获取，若上下文尚未初始化则返回默认加载器。</p>
     *
     * @return ResourceLoader，非 {@code null}
     */
    public static ResourceLoader getResourceLoader() {
        try {
            return SpringUtils.getBeanProvider(ResourceLoader.class)
                    .getIfAvailable(() -> LazyHolder.DEFAULT_RESOURCE_LOADER);
        } catch (IllegalStateException e) {
            return LazyHolder.DEFAULT_RESOURCE_LOADER;
        }
    }

    /**
     * 加载资源为字节数组。
     *
     * @param location 资源位置（支持 {@code classpath:}、{@code file:} 等协议）
     * @return 资源内容的字节数组
     * @throws IllegalArgumentException 若 {@code location} 为空
     * @throws UncheckedIOException     若读取资源失败
     */
    public static byte[] loadBytes(String location) {
        Assert.hasText(location, "location must not be empty");

        try {
            var resource = getResourceLoader().getResource(location);
            return resource.getContentAsByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 加载资源为字符串（UTF-8）。
     *
     * @param location 资源位置
     * @return 资源内容的字符串
     * @throws IllegalArgumentException 若 {@code location} 为空
     * @throws UncheckedIOException     若读取资源失败
     * @see #loadText(String, Charset)
     */
    public static String loadText(String location) {
        return loadText(location, StandardCharsets.UTF_8);
    }

    /**
     * 加载资源为字符串（指定编码）。
     *
     * @param location 资源位置
     * @param charset  字符编码，{@code null} 时使用 UTF-8
     * @return 资源内容的字符串
     * @throws IllegalArgumentException 若 {@code location} 为空
     * @throws UncheckedIOException     若读取资源失败
     */
    public static String loadText(String location, @Nullable Charset charset) {
        Assert.hasText(location, "location must not be empty");

        try {
            var resource = getResourceLoader().getResource(location);
            return resource.getContentAsString(charset != null ? charset : StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 加载资源为输入流。
     * <p>调用者负责关闭返回的 {@link InputStream}。</p>
     *
     * @param location 资源位置
     * @return 资源内容的输入流，非 {@code null}
     * @throws IllegalArgumentException 若 {@code location} 为空
     * @throws UncheckedIOException     若获取输入流失败
     */
    public static InputStream loadAsInputStream(String location) {
        Assert.hasText(location, "location must not be empty");

        try {
            var resource = getResourceLoader().getResource(location);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ------

    /**
     * 获取资源模式解析器。
     *
     * @return ResourcePatternResolver，非 {@code null}
     */
    public static ResourcePatternResolver getResourcePatternResolver() {
        return ResourcePatternUtils.getResourcePatternResolver(getResourceLoader());
    }

    /**
     * 解析一个或多个资源位置模式，返回所有匹配的资源。
     * <p>支持 Spring 资源模式语法（如 {@code classpath*:*.xml}）。
     * 单个模式解析失败时静默跳过（返回空数组），不阻断其他模式的匹配。</p>
     *
     * @param locationPatterns 资源位置模式，不能为 {@code null} 且不能包含 {@code null} 元素
     * @return 匹配的资源列表，非 {@code null}
     * @throws IllegalArgumentException 若 {@code locationPatterns} 为 {@code null}
     */
    public static List<Resource> resolveResources(String... locationPatterns) {
        Assert.notNull(locationPatterns, "locationPatterns must not be null");
        Assert.noNullElements(locationPatterns, "locationPatterns must not contain null elements");

        var resolver = getResourcePatternResolver();
        return Arrays.stream(locationPatterns)
                .map(pattern -> {
                    try {
                        return resolver.getResources(pattern);
                    } catch (IOException e) {
                        return new Resource[0];
                    }
                })
                .flatMap(Arrays::stream)
                .toList();
    }

    // ------

    /**
     * 延迟初始化持有者。
     * <p>使用初始化-on-demand holder 模式，在首次访问时才创建默认 {@link ResourceLoader}。</p>
     */
    private static class LazyHolder {
        private static final ResourceLoader DEFAULT_RESOURCE_LOADER =
                ApplicationResourceLoader.get();
    }

}

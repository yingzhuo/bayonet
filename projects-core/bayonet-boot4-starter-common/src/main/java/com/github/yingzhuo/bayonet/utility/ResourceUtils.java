package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.io.ApplicationResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 资源加载工具类。
 * <p>提供便捷的资源读取方法，优先使用 Spring 上下文中的 {@link ResourceLoader}，
 * 不可用时回退到 {@link ApplicationResourceLoader}。</p>
 *
 * <pre>{@code
 * byte[] data = ResourceUtils.loadBytes("classpath:config.properties");
 * String text = ResourceUtils.loadText("file:/tmp/app.log", StandardCharsets.UTF_8);
 * }</pre>
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
        return loadText(location, UTF_8);
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
            return resource.getContentAsString(charset != null ? charset : UTF_8);
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
     * 延迟初始化持有者。
     * <p>使用初始化-on-demand holder 模式，在首次访问时才创建默认 {@link ResourceLoader}。</p>
     */
    private static class LazyHolder {
        private static final ResourceLoader DEFAULT_RESOURCE_LOADER =
                ApplicationResourceLoader.get();
    }

}

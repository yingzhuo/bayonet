package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * {@link Properties} 加载工具类。
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesUtils {

    /**
     * 加载 Properties 文件。
     *
     * @param location 资源位置，支持 classpath:/、file:/ 等 Spring 资源路径
     * @return {@link Properties} 实例
     * @throws IllegalArgumentException 若 {@code location} 为空
     * @throws UncheckedIOException     若读取或解析失败
     */
    public static Properties loadProperties(String location) {
        return loadProperties(location, false);
    }

    /**
     * 加载 Properties 文件。
     *
     * @param location 资源位置，支持 classpath:/、file:/ 等 Spring 资源路径
     * @param xmlFormat 是否为 XML 格式。{@code true} 使用 {@link Properties#loadFromXML}，
     *                  {@code false} 使用 {@link Properties#load}
     * @return {@link Properties} 实例
     * @throws IllegalArgumentException 若 {@code location} 为空
     * @throws UncheckedIOException     若读取或解析失败
     */
    public static Properties loadProperties(String location, boolean xmlFormat) {
        Assert.hasText(location, "location must not be empty");

        try (var stream = ResourceUtils.loadAsInputStream(location)) {
            var properties = new Properties();
            if (xmlFormat) {
                properties.loadFromXML(stream);
            } else {
                properties.load(stream);
            }
            return properties;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@link Properties} 与 {@link Map} 之间的转换工具类。
 * <p>所有方法接受 {@code null} 输入，返回不可变 Map。</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesUtils {

    /**
     * 将 {@link Properties} 转为 {@code Map<String, Object>}。
     *
     * @param properties 源属性集，可为 {@code null}
     * @return 不可变的 {@code Map<String, Object>}（非 {@code null}）
     */
    public static Map<String, Object> toStringObjectMap(@Nullable Properties properties) {
        if (properties == null) {
            return Map.of();
        }
        var map = new HashMap<String, Object>(properties.size());
        for (var key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 将 {@link Properties} 转为 {@code Map<String, String>}。
     *
     * @param properties 源属性集，可为 {@code null}
     * @return 不可变的 {@code Map<String, String>}（非 {@code null}）
     */
    public static Map<String, String> toStringMap(@Nullable Properties properties) {
        if (properties == null) {
            return Map.of();
        }
        var map = new HashMap<String, String>(properties.size());
        for (var key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 将 {@link Properties} 包装为 {@link MapPropertySource}。
     *
     * @param name       PropertySource 名称（非空）
     * @param properties 源属性集，可为 {@code null}
     * @return {@link MapPropertySource}（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code name} 为空
     */
    public static MapPropertySource toMapPropertySource(String name, @Nullable Properties properties) {
        Assert.hasText(name, "name must not be empty");
        return new MapPropertySource(name, toStringObjectMap(properties));
    }

}

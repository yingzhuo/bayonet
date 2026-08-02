package com.github.yingzhuo.bayonet.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 嵌套 {@link Map} 展平工具类。
 * <p>将嵌套的 Map/集合结构递归展平为带 {@code .} 或 {@code [n]} 路径的扁平 Map，
 * 供 HOCON、TOML 等嵌套配置格式的配置加载器使用。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapFlattenUtils {

    /**
     * 将嵌套 Map 展平为扁平 Map。
     *
     * @param source 嵌套结构（非 {@code null}）
     * @return 展平后的 Map（非 {@code null}）
     */
    public static Map<String, Object> flatten(Map<String, Object> source) {
        var result = new LinkedHashMap<String, Object>();
        flatten(result, source, null);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void flatten(Map<String, Object> result, Map<String, Object> source, @Nullable String root) {
        var rootHasText = StringUtils.hasText(root);

        source.forEach((key, value) -> {
            var path = rootHasText
                    ? (key.startsWith("[") ? root + key : root + "." + key)
                    : key;

            if (value instanceof Map) {
                var map = (Map<String, Object>) value;
                flatten(result, map, path);
            } else if (value instanceof Collection) {
                var collection = (Collection<Object>) value;
                var count = 0;
                for (var object : collection) {
                    flatten(result, Collections.singletonMap("[" + (count++) + "]", object), path);
                }
            } else {
                result.put(path, value != null ? value : "");
            }
        });
    }
}

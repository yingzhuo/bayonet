package com.github.yingzhuo.bayonet.hocon;

import com.typesafe.config.ConfigFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * HOCON（Human-Optimized Config Object Notation）配置文件的 {@link PropertySourceLoader} 实现。
 * <p>负责将 {@code .conf} 文件解析为 Spring 的 {@link MapPropertySource}，
 * 支持嵌套 map 和集合结构的递归展平。</p>
 * @author 应卓
 */
public class HoconPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"conf"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        var config = ConfigFactory.parseURL(resource.getURL());

        var result = new LinkedHashMap<String, Object>();
        buildFlattenedMap(result, config.root().unwrapped(), null);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new MapPropertySource(name, result));
    }

    @SuppressWarnings("unchecked")
    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String root) {
        var rootHasText = StringUtils.hasText(root);

        source.forEach((key, value) -> {
            var path = rootHasText
                    ? (key.startsWith("[") ? root + key : root + "." + key)
                    : key;

            if (value instanceof Map) {
                var map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, path);
            } else if (value instanceof Collection) {
                var collection = (Collection<Object>) value;
                var count = 0;
                for (var object : collection) {
                    buildFlattenedMap(result, Collections.singletonMap("[" + (count++) + "]", object), path);
                }
            } else {
                result.put(path, value != null ? value : "");
            }
        });
    }
}

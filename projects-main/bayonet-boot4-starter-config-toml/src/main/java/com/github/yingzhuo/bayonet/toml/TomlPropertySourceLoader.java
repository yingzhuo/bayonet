package com.github.yingzhuo.bayonet.toml;

import com.github.yingzhuo.bayonet.config.MapFlattenUtils;
import com.moandjiezana.toml.Toml;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * TOML 配置文件的 {@link PropertySourceLoader} 实现。
 * <p>负责将 {@code .toml} 文件解析为 Spring 的 {@link MapPropertySource}，
 * 支持嵌套表（table）和数组结构的递归展平（见 {@link MapFlattenUtils}）。</p>
 *
 * @author 应卓
 * @see MapFlattenUtils
 * @since 4.1.1
 */
public class TomlPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"toml"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        try (var in = resource.getInputStream()) {
            var map = new Toml().read(in).toMap();
            var result = MapFlattenUtils.flatten(map);

            if (result.isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.singletonList(new MapPropertySource(name, result));
        }
    }
}

package com.github.yingzhuo.bayonet.hocon;

import com.github.yingzhuo.bayonet.config.MapFlattenUtils;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * HOCON（Human-Optimized Config Object Notation）配置文件的 {@link PropertySourceLoader} 实现。
 * <p>负责将 {@code .conf} 文件解析为 Spring 的 {@link MapPropertySource}，
 * 支持嵌套 map 和集合结构的递归展平（见 {@link MapFlattenUtils}）。</p>
 *
 * @author 应卓
 * @see MapFlattenUtils
 * @since 4.1.0
 */
public class HoconPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"conf"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        var config = ConfigFactory.parseURL(resource.getURL());
        var result = MapFlattenUtils.flatten(config.root().unwrapped());

        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new MapPropertySource(name, result));
    }
}

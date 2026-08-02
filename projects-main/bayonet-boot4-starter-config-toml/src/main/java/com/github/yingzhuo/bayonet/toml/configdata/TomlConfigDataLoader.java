package com.github.yingzhuo.bayonet.toml.configdata;

import com.github.yingzhuo.bayonet.toml.TomlPropertySourceLoader;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 加载 {@link TomlConfigDataResource} 为 {@link ConfigData}。
 * <p>复用 {@link TomlPropertySourceLoader} 解析 TOML 内容，将展平后的属性包装为
 * {@link ConfigData}。</p>
 *
 * @author 应卓
 * @see TomlConfigDataLocationResolver
 * @since 4.1.1
 */
public class TomlConfigDataLoader implements ConfigDataLoader<TomlConfigDataResource> {

    private final PropertySourceLoader loader = new TomlPropertySourceLoader();

    @Override
    public ConfigData load(ConfigDataLoaderContext context, TomlConfigDataResource resource) throws IOException {
        var name = resource.getResource().getFilename();
        if (!StringUtils.hasText(name)) {
            name = "TOML config";
        }
        return new ConfigData(loader.load(name, resource.getResource()));
    }
}

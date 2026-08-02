package com.github.yingzhuo.bayonet.hocon.configdata;

import com.github.yingzhuo.bayonet.hocon.HoconPropertySourceLoader;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 加载 {@link HoconConfigDataResource} 为 {@link ConfigData}。
 * <p>复用 {@link HoconPropertySourceLoader} 解析 HOCON 内容，将展平后的属性包装为
 * {@link ConfigData}。</p>
 *
 * @author 应卓
 * @see HoconConfigDataLocationResolver
 * @since 4.1.1
 */
public class HoconConfigDataLoader implements ConfigDataLoader<HoconConfigDataResource> {

    private final PropertySourceLoader loader = new HoconPropertySourceLoader();

    @Override
    public ConfigData load(ConfigDataLoaderContext context, HoconConfigDataResource resource) throws IOException {
        var name = resource.getResource().getFilename();
        if (!StringUtils.hasText(name)) {
            name = "HOCON config";
        }
        return new ConfigData(loader.load(name, resource.getResource()));
    }
}

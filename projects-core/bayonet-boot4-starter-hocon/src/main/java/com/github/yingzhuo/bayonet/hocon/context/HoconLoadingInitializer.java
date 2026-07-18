package com.github.yingzhuo.bayonet.hocon.context;

import com.github.yingzhuo.bayonet.context.AbstractApplicationContextInitializer;
import com.github.yingzhuo.bayonet.hocon.HoconPropertySourceLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.io.IOException;

/**
 * 在应用上下文刷新前从默认路径加载 HOCON 配置文件的初始化器。
 * <p>按优先级依次尝试以下路径，找到第一个可读的文件即加载：</p>
 * <ol>
 *   <li>{@code file:default.conf}</li>
 *   <li>{@code file:config/default.conf}</li>
 *   <li>{@code classpath:default.conf}</li>
 *   <li>{@code classpath:config/default.conf}</li>
 * </ol>
 * <p>若所有路径均不可用则静默跳过（设计上配置文件是可选的）。</p>
 *
 * @see com.github.yingzhuo.bayonet.context.PropertiesLoadingInitializer
 * @author 应卓
 */
@Slf4j
public class HoconLoadingInitializer extends AbstractApplicationContextInitializer<ConfigurableApplicationContext>
        implements Ordered {

    private static final String[] DEFAULT_LOCATIONS = new String[]{
            "file:default.conf",
            "file:config/default.conf",
            "classpath:default.conf",
            "classpath:config/default.conf"
    };

    private static final PropertySourceLoader LOADER = new HoconPropertySourceLoader();

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        for (var location : DEFAULT_LOCATIONS) {
            var resource = loadResource(ctx, location);
            if (resource == null) {
                log.trace("HOCON config not found at: {}", location);
                continue;
            }

            try {
                var propertySourceList = LOADER.load(location, resource);
                for (var propertySource : propertySourceList) {
                    ctx.getEnvironment().getPropertySources().addFirst(propertySource);
                }
                log.debug("loaded HOCON config from: {}", location);
                break;
            } catch (IOException e) {
                log.warn("failed to load HOCON config from {}: {}", location, e.getMessage());
            }
        }
    }

    @Override
    public int getOrder() {
        return 110;
    }

}

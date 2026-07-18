package com.github.yingzhuo.bayonet.hocon.context;

import com.github.yingzhuo.bayonet.hocon.HoconPropertySourceLoader;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

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
 */
@Slf4j
public class HoconLoadingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

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
            var resource = getResource(ctx, location);
            if (resource == null) {
                log.trace("HOCON config not found at: {}", location);
                continue;
            }

            try {
                var propertySourceList = LOADER.load(location, resource);
                for (var propertySource : propertySourceList) {
                    ctx.getEnvironment().getPropertySources().addLast(propertySource);
                }
                log.debug("Loaded HOCON config from: {}", location);
                return;
            } catch (IOException e) {
                log.warn("Failed to load HOCON config from {}: {}", location, e.getMessage());
            }
        }
    }

    /**
     * 检查资源是否存在且可读。
     *
     * @param resourceLoader ResourceLoader
     * @param location       资源路径
     * @return 可读的 Resource，不存在或不可读返回 {@code null}
     */
    private @Nullable Resource getResource(ResourceLoader resourceLoader, String location) {
        var resource = resourceLoader.getResource(location);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        return null;
    }

}

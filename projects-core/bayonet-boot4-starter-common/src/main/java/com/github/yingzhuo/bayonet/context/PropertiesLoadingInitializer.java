package com.github.yingzhuo.bayonet.context;

import com.github.yingzhuo.bayonet.utility.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Properties;

/**
 * 在 Spring 容器初始化前加载外部 properties 文件，将其注册到 {@code Environment} 的 {@code PropertySource} 末尾。
 * <p>按优先级依次尝试以下路径，找到第一个可读的文件即加载：</p>
 * <ol>
 *   <li>{@code file:default.properties}</li>
 *   <li>{@code file:config/default.properties}</li>
 *   <li>{@code classpath:default.properties}</li>
 *   <li>{@code classpath:config/default.properties}</li>
 * </ol>
 * <p>若所有路径均不可用则静默跳过（设计上配置文件是可选的）。</p>
 * @author 应卓
 */
@Slf4j
public class PropertiesLoadingInitializer extends AbstractApplicationContextInitializer<ConfigurableApplicationContext>
        implements Ordered {

    private static final String[] DEFAULT_LOCATIONS = new String[]{
            "file:default.properties",
            "file:config/default.properties",
            "classpath:default.properties",
            "classpath:config/default.properties"
    };

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        for (String location : DEFAULT_LOCATIONS) {
            var resource = loadResource(ctx, location);
            if (resource == null) {
                log.trace("properties config not found at: {}", location);
                continue;
            }

            try (var stream = resource.getInputStream()) {
                var properties = new Properties();
                properties.load(stream);

                ctx.getEnvironment()
                        .getPropertySources()
                        .addFirst(PropertiesUtils.toMapPropertySource(location, properties));
                log.debug("loaded properties from {}", location);
                break;
            } catch (IOException e) {
                log.warn("failed to load properties from {}: {}", location, e.getMessage());
            }
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }
}

package com.github.yingzhuo.bayonet.context;

import com.github.yingzhuo.bayonet.utility.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Properties;

/**
 * 在 Spring 容器初始化前加载外部 properties 文件，将其注册到 {@code Environment} 的 {@code PropertySource} 末尾。
 * <p>默认按以下顺序查找 {@code default.properties}</p>
 * <ol>
 *   <li>{@code file:default.properties}</li>
 *   <li>{@code file:config/default.properties}</li>
 *   <li>{@code classpath:default.properties}</li>
 *   <li>{@code classpath:config/default.properties}</li>
 *   <li>{@code classpath:META-INF/default.properties}</li>
 * </ol>
 *
 * <p>适用于需要在 application.yml 加载前注入默认属性的场景。
 * 当某位置的文件不存在或不可读时静默跳过。</p>
 */
@Slf4j
public class PropertiesLoadingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String[] DEFAULT_LOCATIONS = new String[]{
            "file:default.properties",
            "file:config/default.properties",
            "classpath:default.properties",
            "classpath:config/default.properties",
            "classpath:META-INF/default.properties",
    };

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        for (String location : DEFAULT_LOCATIONS) {
            var resource = getResource(ctx, location);
            if (resource == null) {
                continue;
            }

            try (var stream = resource.getInputStream()) {
                var properties = new Properties();
                properties.load(stream);

                ctx.getEnvironment()
                        .getPropertySources()
                        .addLast(PropertiesUtils.toMapPropertySource(location, properties));

                log.debug("Loaded properties from {}", location);

            } catch (IOException e) {
                log.warn("Failed to load properties from {}: {}", location, e.getMessage());
            }
        }
    }

    private @Nullable Resource getResource(ResourceLoader resourceLoader, String location) {
        var resource = resourceLoader.getResource(location);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        return null;
    }

}

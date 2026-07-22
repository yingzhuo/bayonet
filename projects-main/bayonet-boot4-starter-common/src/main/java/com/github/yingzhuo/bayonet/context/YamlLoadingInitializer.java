package com.github.yingzhuo.bayonet.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 在 Spring 容器初始化前加载 YAML 配置文件的初始化器。
 * <p>按优先级依次尝试以下路径，找到第一个可读的文件即加载，同时支持 {@code .yaml} 和 {@code .yml} 后缀：</p>
 * <ol>
 *   <li>{@code file:default.yaml}</li>
 *   <li>{@code file:default.yml}</li>
 *   <li>{@code file:config/default.yaml}</li>
 *   <li>{@code file:config/default.yml}</li>
 *   <li>{@code classpath:default.yaml}</li>
 *   <li>{@code classpath:default.yml}</li>
 *   <li>{@code classpath:config/default.yaml}</li>
 *   <li>{@code classpath:config/default.yml}</li>
 * </ol>
 * <p>若 {@code spring.application.name} 已配置，还会按 {@code classpath -> classpath:config -> file -> file:config}
 * 优先级尝试加载 {@code {应用名}.yaml} 和 {@code {应用名}.yml}。</p>
 * <p>若所有路径均不可用则静默跳过（设计上配置文件是可选的）。</p>
 *
 * @author 应卓
 * @see PropertiesLoadingInitializer
 * @since 4.1.1
 */
@Slf4j
public class YamlLoadingInitializer extends AbstractApplicationContextInitializer<ConfigurableApplicationContext>
        implements Ordered {

    private static final List<String> DEFAULT_LOCATIONS = List.of(
            "file:default.yaml",
            "file:default.yml",
            "file:config/default.yaml",
            "file:config/default.yml",
            "classpath:default.yaml",
            "classpath:default.yml",
            "classpath:config/default.yaml",
            "classpath:config/default.yml"
    );

    private static final PropertySourceLoader LOADER = new YamlPropertySourceLoader();

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        var locationList = new ArrayList<>(DEFAULT_LOCATIONS);

        var applicationName = ctx.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.hasText(applicationName)) {
            locationList.add("classpath:" + applicationName + ".yaml");
            locationList.add("classpath:" + applicationName + ".yml");
            locationList.add("classpath:config/" + applicationName + ".yaml");
            locationList.add("classpath:config/" + applicationName + ".yml");
            locationList.add("file:" + applicationName + ".yaml");
            locationList.add("file:" + applicationName + ".yml");
            locationList.add("file:config/" + applicationName + ".yaml");
            locationList.add("file:config/" + applicationName + ".yml");
        }

        for (var location : locationList) {
            var resource = loadResource(ctx, location);
            if (resource == null) {
                log.trace("YAML config not found at: {}", location);
                continue;
            }

            try {
                var propertySourceList = LOADER.load(location, resource);
                for (var propertySource : propertySourceList) {
                    ctx.getEnvironment().getPropertySources().addFirst(propertySource);
                }
                log.debug("loaded YAML config from: {}", location);
                break;
            } catch (IOException | RuntimeException e) {
                log.warn("failed to load YAML config from {}: {}", location, e.getMessage());
            }
        }
    }

    @Override
    public int getOrder() {
        return 90;
    }
}

package com.github.yingzhuo.bayonet.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 在 Spring 容器初始化前加载外部 properties 文件，将其注册到 {@code Environment} 的 {@code PropertySource} 中。
 * <p>按优先级依次尝试以下路径，找到第一个可读的文件即加载：</p>
 * <ol>
 *   <li>{@code file:default.properties}</li>
 *   <li>{@code file:config/default.properties}</li>
 *   <li>{@code classpath:default.properties}</li>
 *   <li>{@code classpath:config/default.properties}</li>
 * </ol>
 * <p>若 {@code spring.application.name} 已配置，还会尝试加载 {@code {应用名}.properties}。</p>
 * <p>若所有路径均不可用则静默跳过（设计上配置文件是可选的）。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Slf4j
public class PropertiesLoadingInitializer extends AbstractApplicationContextInitializer implements Ordered {

    private static final List<String> DEFAULT_LOCATIONS = List.of(
            "file:default.properties",
            "file:config/default.properties",
            "classpath:default.properties",
            "classpath:config/default.properties"
    );

    private static final PropertySourceLoader LOADER = new PropertiesPropertySourceLoader();

    public PropertiesLoadingInitializer() {
        setOrder(110);
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        var locationList = new ArrayList<>(DEFAULT_LOCATIONS);

        var applicationName = ctx.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.hasText(applicationName)) {
            locationList.add("classpath:" + applicationName + ".properties");
            locationList.add("classpath:config/" + applicationName + ".properties");
            locationList.add("file:" + applicationName + ".properties");
            locationList.add("file:config/" + applicationName + ".properties");
        }

        var resource = super.findFirstExistingResource(ctx, locationList);

        if (resource == null) {
            return;
        }

        var name = super.getResourceFilenameOrElse(resource, "Properties config");

        try {
            for (var propertySource : LOADER.load(name, resource)) {
                ctx.getEnvironment().getPropertySources().addFirst(propertySource);
            }
            log.debug("loaded properties from: {}", name);
        } catch (Exception e) {
            log.warn("failed to load properties from {}: {}", name, e.getMessage());
        }
    }
}
